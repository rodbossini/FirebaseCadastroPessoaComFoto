package br.com.bossini.firebasecadastropessoacomfoto;

/**
 * Created by rodrigo on 9/3/17.
 */


public class Pessoa {
    private String cpf;
    private String nome;
    private int idade;
    private String fotoURL;

    public Pessoa (){}

    public Pessoa (String cpf, String nome, int idade){
        this.setCpf(cpf);
        this.setNome(nome);
        this.setIdade(idade);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }


}


