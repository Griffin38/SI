#firstup <- function(x) {
# substr(x, 1, 1) <- toupper(substr(x, 1, 1))
#x
#}
#firstup(tolower("FAFE"))
setwd("/Users/gil/Documents/cadeiras/SI/trabalho novais/dados_copia/despesa/")

cenas2010<-read.csv("AIIIDM2010.csv", header = TRUE,stringsAsFactors=FALSE)
cenas2011<-read.csv("AIIIDM2011.csv", header = TRUE,stringsAsFactors=FALSE)
cenas2012<-read.csv("AIIIDM2012.csv", header = TRUE,stringsAsFactors=FALSE)
cenas2013<-read.csv("AIIIDM2013.csv", header = TRUE,stringsAsFactors=FALSE)
cenas2014<-read.csv("AIIIDM2014.csv", header = TRUE,stringsAsFactors=FALSE)
cenas2015<-read.csv("AIIIDM2015.csv", header = TRUE,stringsAsFactors=FALSE)




cenas1=c("")

cenas2=c(0)

cenas2010[,"Regiao"]<-cenas1
cenas2011[,"Regiao"]<-cenas1
cenas2012[,"Regiao"]<-cenas1
cenas2013[,"Regiao"]<-cenas1
cenas2014[,"Regiao"]<-cenas1
cenas2015[,"Regiao"]<-cenas1

cenas2010[,"Data"]<-cenas2
cenas2011[,"Data"]<-cenas2
cenas2012[,"Data"]<-cenas2
cenas2013[,"Data"]<-cenas2
cenas2014[,"Data"]<-cenas2
cenas2015[,"Data"]<-cenas2



regiao_norte<-c("Amares", "Barcelos", "Braga", "Cabeceiras de Basto", 
                "Celorico de Basto", "Esposende", "Fafe","Vizela", 
                "Guimarães", "Póvoa de Lanhoso", "Terras do Bouro", 
                "Vieira do Minho", "Vila Nova de Famalicão", "Vila Verde",
                "Arcos de Valdevez", "Caminha", "Melgaço", "Monção", 
                "Paredes de Coura", "Ponte da Barca", "Ponte de Lima", "Valença", 
                "Viana do Castelo", "Vila Nova de Cerveira",
                "Arouca","Espinho","Gondomar","Maia","Matosinhos",
                "Oliveira de Azeméis","Paredes","Porto","Póvoa de Varzim",
                "Santa Maria da Feira","Santo Tirso","São João da Madeira",
                "Trofa","Vale de Cambra","Valongo","Vila do Conde",
                "Vila Nova de Gaia","Boticas","Chaves","Montalegre",
                "Ribeira de Pena","Valpaços","Vila Pouca de Aguiar",
                "Mondim de Basto","Terras de Bouro","Alijó","Armamar",
                "Carrazeda de Ansiães","Freixo de Espada à Cinta",
                "Lamego","Mesão Frio","Moimenta da Beira",
                "Murça","Penedono","Peso da Régua","Sabrosa",
                "Santa Marta de Penaguião","São João da Pesqueira"
                ,"Sernancelhe","Tabuaço","Tarouca","Torre de Moncorvo"
                ,"Vila Nova de Foz Côa","Vila Real","Resende","Amarante",
                "Baião","Castelo de Paiva","Cinfães","Felgueiras",
                "Lousada","Marco de Canaveses","Paços de Ferreira",
                "Penafiel","Resende","Alfândega da Fé","Bragança",
                "Macedo de Cavaleiros","Miranda do Douro","Mirandela",
                "Mogadouro","Vila Flor","Vimioso","Vinhais")


for(j in 1:length(regiao_norte)) {
array<-which(cenas2010 ==toupper(regiao_norte[j]), arr.ind = T)
if(length(array)!=0) {
for(i in 1:length(array[,"row"])) {
cenas2010[array[i,1],"Regiao"]<-"Norte"
}
}
}

for(j in 1:length(regiao_norte)) {
  array<-which(cenas2011 ==toupper(regiao_norte[j]), arr.ind = T)
  if(length(array)!=0) {
    for(i in 1:length(array[,"row"])) {
      cenas2011[array[i,1],"Regiao"]<-"Norte"
    }
  }
}

for(j in 1:length(regiao_norte)) {
  array<-which(cenas2012 ==toupper(regiao_norte[j]), arr.ind = T)
  if(length(array)!=0) {
    for(i in 1:length(array[,"row"])) {
      cenas2012[array[i,1],"Regiao"]<-"Norte"
    }
  }
}

for(j in 1:length(regiao_norte)) {
  array<-which(cenas2013 ==toupper(regiao_norte[j]), arr.ind = T)
  if(length(array)!=0) {
    for(i in 1:length(array[,"row"])) {
      cenas2013[array[i,1],"Regiao"]<-"Norte"
    }
  }
}

for(j in 1:length(regiao_norte)) {
  array<-which(cenas2014 ==toupper(regiao_norte[j]), arr.ind = T)
  if(length(array)!=0) {
    for(i in 1:length(array[,"row"])) {
      cenas2014[array[i,1],"Regiao"]<-"Norte"
    }
  }
}

for(j in 1:length(regiao_norte)) {
  array<-which(cenas2015 ==toupper(regiao_norte[j]), arr.ind = T)
  if(length(array)!=0) {
    for(i in 1:length(array[,"row"])) {
      cenas2015[array[i,1],"Regiao"]<-"Norte"
    }
  }
}






cidades2010<-cenas2010[cenas2010$Regiao=="Norte",]
cidades2010[,"Data"]<-2010
cidades2010<-cidades2010[,-c(2,3,6,18,12,17)]

cidades2011<-cenas2011[cenas2011$Regiao=="Norte",]
cidades2011[,"Data"]<-2011
cidades2011<-cidades2011[,-c(2,3,6,18,12,17)]

cidades2012<-cenas2012[cenas2012$Regiao=="Norte",]
cidades2012[,"Data"]<-2012
cidades2012<-cidades2012[,-c(2,3,6,18,12,17)]

cidades2013<-cenas2013[cenas2013$Regiao=="Norte",]
cidades2013[,"Data"]<-2013
cidades2013<-cidades2013[,-c(2,3,6,18,12,17)]

cidades2014<-cenas2014[cenas2014$Regiao=="Norte",]
cidades2014[,"Data"]<-2014
cidades2014<-cidades2014[,-c(2,3,6,18,12,17)]

cidades2015<-cenas2015[cenas2015$Regiao=="Norte",]
cidades2015[,"Data"]<-2015
cidades2015<-cidades2015[,-c(2,3,6,18,12,17)]

remove(cenas2010,cenas2011,cenas2012,cenas2013,cenas2014,cenas2015,
       cenas1,regiao_norte,cenas2)


test <- cidades2015
treino<-rbind(cidades2010,cidades2011,cidades2012,cidades2013,cidades2014)



remove(cidades2010,cidades2011,cidades2012,cidades2013,cidades2014,cidades2015,
       array,i,j)


for(j in 1:4) {
  
  array<-which(treino ==paste("T",j,sep = ""), arr.ind = T)
  
  if(length(array)>0) {
    
      treino<-treino[-array[,1],]
    
  }
}  

for(j in 1:4) {
  
  array<-which(test ==paste("T",j,sep = ""), arr.ind = T)
  
  if(length(array)>0) {
    
    test<-test[-array[,1],]
    
  }
}  


treino<-treino[,-c(7,12)]
test<-test[,-c(7,12)]

formula=DESPESA_TOTAL ~ DESPESA_AQUISICAO_BENS+
  INFRAESTRUTURAS_BASICAS+
  ACESSIBILIDADES+
  JUROS_ENCARGOS+
  DESPESA_COM_PESSOAL+
  OUTROS_INVESTIMENTOS_BENS_DE_CAPITAL+
  DESPESA_CORRENTE+
  CODIGOINE+
  TRANSFERENCIAS_OUTRAS_DESPESAS_CAPITAL+
  Data






fit<- glm(formula ,family = gaussian,data = treino)

#fit$coefficients

#residuals(fit)


predit.model<-predict(fit,newdata=test,type="response")

MSE.lm <- sum((predit.model - test$DESPESA_TOTAL)^2)/nrow(test)

data<-rbind(treino,test)
#summary(predit.model)



maxs <- apply(data, 2, max) 
mins <- apply(data, 2, min)

scaled <- as.data.frame(scale(treino, center = mins, scale = maxs - mins))

library(neuralnet)
nn <- neuralnet(formula,data=scaled,hidden=c(10,50,50,50,10,10),linear.output=T)
#plot(nn)

#table(treino[,2])

scaled_test <- as.data.frame(scale(test, center = mins, scale = maxs - mins))

creditnet.results<- compute(nn,scaled_test[,-c(4)])



pr.nn_ <- creditnet.results$net.result*(max(data$DESPESA_TOTAL)-min(data$DESPESA_TOTAL))+min(data$DESPESA_TOTAL)
test.r <- (scaled_test$DESPESA_TOTAL)*(max(data$DESPESA_TOTAL)-min(data$DESPESA_TOTAL))+min(data$DESPESA_TOTAL)

MSE.nn <- sum((test.r - pr.nn_)^2)/nrow(scaled_test)

print(paste(MSE.lm,MSE.nn))



par(mfrow=c(1,2))

plot(test$DESPESA_TOTAL,pr.nn_,col='red',main='Real vs predicted NN',pch=18,cex=0.7)
abline(0,1,lwd=2)
legend('bottomright',legend='NN',pch=18,col='red', bty='n')

plot(test$DESPESA_TOTAL,predit.model,col='blue',main='Real vs predicted lm',pch=18, cex=0.7)
abline(0,1,lwd=2)
legend('bottomright',legend='LM',pch=18,col='blue', bty='n', cex=.95)

