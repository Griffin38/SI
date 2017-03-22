package src;


import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Player  extends Agent{
        private double dinheiro;
        
       
    
        
        @Override
    protected void setup() {
        super.setup();
        this.addBehaviour(new ReceiveBehaviour());
        
        
        }
    

        
    private class ReceiveBehaviour extends CyclicBehaviour {
		
		@Override
		public void action(){
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology("DINHEIRO");
			MessageTemplate mtRespJogo = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtRespJogo);
			
			if(msg != null){
				
				try {
					dinheiro = Double.parseDouble( msg.getContent());
					
                                        
                                                

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