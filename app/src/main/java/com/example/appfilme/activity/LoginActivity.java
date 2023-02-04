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
import com.example.appfilme.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();

        //Fazer login do usuário
        progressBar.setVisibility(View.GONE);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senha = campoSenha.getText().toString();
                String email = campoEmail.getText().toString();
                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);

                        validarLogin(usuario);



                    }else{
                        Toast.makeText(LoginActivity.this, "preencha a senha!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void validarLogin(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), PublicacoesActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"Erro ao fazer login",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
    }
    public void abrirCadastro (View view){
        Intent i = new Intent(LoginActivity.this, Cadastro2Activity.class);
        startActivity(i);
    }
    public void inicializarComponentes(){
        campoEmail = findViewById(R.id.editLoginEmail1);
        campoSenha = findViewById(R.id.editLoginSenha1);
        botaoEntrar = findViewById(R.id.buttonEntrar1);
        progressBar=findViewById(R.id.progressBarLogin);

        campoEmail.requestFocus();
    }
}