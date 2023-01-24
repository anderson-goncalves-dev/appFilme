package com.example.appfilme.model;

import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Publicacao {
    private String idPublicacao;
    private String categoria;
    private  String titulo;
    private String descricao;
    private List<String> fotos;

    public Publicacao() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase().child("minhas_publicacoes");
        setIdPublicacao(anuncioRef.push().getKey());
    }
    public void salvar(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference publicacaoRef = ConfiguracaoFirebase.getFirebase().child("minhas_publicacoes");
        setIdPublicacao(publicacaoRef.push().getKey());
        publicacaoRef.child(idUsuario).child(getIdPublicacao()).setValue(this);

        salvarPublicacaoPublico();
    }
    public void salvarPublicacaoPublico(){

        DatabaseReference publicacaoRef = ConfiguracaoFirebase.getFirebase().child("publicacoes");
        setIdPublicacao(publicacaoRef.push().getKey());
        publicacaoRef.child(getCategoria()).child(getIdPublicacao()).setValue(this);
    }

    public String getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(String idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
