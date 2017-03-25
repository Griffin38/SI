package main;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import cartas.IPlayer;
import java.io.Serializable;

/**
 *
 * @author gil
 */
public class Argumentos implements Serializable{
    private double dinheiro;
    private IPlayer jogador;

    public Argumentos(double dinheiro, IPlayer jogador) {
        this.dinheiro = dinheiro;
        this.jogador = jogador;
    }

    
    
    
    
    
    
    public double getDinheiro() {
        return dinheiro;
    }

    public void setDinheiro(double dinheiro) {
        this.dinheiro = dinheiro;
    }

    public IPlayer getJogador() {
        return jogador;
    }

    public void setJogador(IPlayer jogador) {
        this.jogador = jogador;
    }
    
    
    
    
    
    
    
}
