package src;


import cartas.Card;
import cartas.IPlayer;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;


public class AgentPlayer  extends Agent{
        private double dinheiro;
        private int nrAgentesFold;
        private IPlayer jogador;
       
    
        
        @Override
    protected void setup() {
        super.setup();
        this.nrAgentesFold=0;
        this.dinheiro=0;
        this.addBehaviour(new ReceiveBehaviourJogador() );
       
        
        }
    

        
   
    /*
    Recebe mensagens dos agentes que vão desistindo da partida
    */
    
    private class ReceiveBehaviourAgenteInativos extends CyclicBehaviour {
                
		
		@Override
		public void action(){
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.DESISTIU);
			MessageTemplate mtRespJogo = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtRespJogo);
			
			if(msg != null){
				
				try {
					nrAgentesFold++;
					
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
				
			}

        
		}
    
  
    private class ReceiveBehaviourJogador extends  CyclicBehaviour {
                
		
		@Override
		public void action(){
			Argumentos a;
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.JOGADOR);
			MessageTemplate mtRespJogo = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtRespJogo);
			
			if(msg != null){
				
				try {
					a= (Argumentos) msg.getContentObject();
                                        dinheiro=a.getDinheiro();
                                        jogador = a.getJogador();
                                        
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
				
			}

        
		}
    
    

    
    
    
    
    
    
    
    
    public double getDinheiro() {
        return dinheiro;
    }

    public void setDinheiro(double dinheiro) {
        this.dinheiro = dinheiro;
    }
	
    
    public void retirarDinheiro(double quantidade) {
    
        this.dinheiro=this.dinheiro-quantidade;
    }
    
    
    public void aumentaDinheiro(double quantidade) {
    
        this.dinheiro=this.dinheiro+quantidade;
        
    }
    
    
    
    
   
    
    
    
    

}