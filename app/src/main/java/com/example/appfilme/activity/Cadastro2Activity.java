package com.example.appfilme.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appfilme.R;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.helper.UsuarioFirebase;
import com.example.appfilme.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class Cadastro2Activity extends AppCompatActivity {
    private EditText campoEmail, campoSenha, campoUsuario;
    private Button botaoCadastrar;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);
        inicializarComponentes();

        //CadastrarUsuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                String nome = campoUsuario.getText().toString();

                if(!nome.isEmpty()){
                    if(!email.isEmpty()){
                        if(!senha.isEmpty()){
                            usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setSenha(senha);
                            usuario.setEmail(email);
                            cadastrar(usuario);

                        }else{
                            Toast.makeText(Cadastro2Activity.this, "preencha a senha!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(Cadastro2Activity.this, "preencha o email!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Cadastro2Activity.this, "preencha o usuário!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Metodo responsável por cadastrar e fazer validações
    public void cadastrar(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            try {
                                progressBar.setVisibility(View.GONE);
                                //Salvar dados no firebase
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.salvar();

                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                Toast.makeText(Cadastro2Activity.this, "Cadastro realizado com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),PublicacoesActivity.class));
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else{
                            progressBar.setVisibility(View.GONE);
                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Esta conta já foi cadastrada";

                            }catch (Exception e){
                                erroExcecao= "Ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(Cadastro2Activity.this,"Erro: "+ erroExcecao ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail1);
        campoSenha = findViewById(R.id.editCadastroSenha1);
        campoUsuario=findViewById(R.id.editCadastroNome);
        botaoCadastrar = findViewById(R.id.buttonCadastrar1);
        progressBar=findViewById(R.id.prgressBarCadastro);

        campoUsuario.requestFocus();
    }

}