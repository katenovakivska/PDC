package Task5;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author Kateryna
 */
public class ParallelBank {
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        for (int i = 0; i < 20; i++) {
            PetriObjModel model = getModel(3, 1500, 0.001, 0.002);

            model.setIsProtokol(false);
            long startTime = System.currentTimeMillis();
            model.go(10000000);
            long stopTime = System.currentTimeMillis();
            System.out.println("Time: " + (stopTime - startTime)+" Amount of transactions: "+(model.getListObj().get(0).getNet().getListP()[6].getMark() - 1));
        }
    }

    public static PetriObjModel getModel(int k,int amountOfTransactions, double lockDelay, double transferDelay) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<PetriSim>();
        list.add(new PetriSim(CreateBankNet(k,amountOfTransactions, lockDelay, transferDelay)));
        for (int i=0; i<k; i++){
            list.add(new PetriSim(CreateUserNet(amountOfTransactions)));
        }
        for (int i=0; i<k; i++) {
            list.get(i).getNet().getListP()[1] = list.get(0).getNet().getListP()[0];
        }

        PetriObjModel model = new PetriObjModel(list);
        return model;
    }
    public static PetriNet CreateBankNet(int k,int amountOfTransactions, double lockDelay, double transferDelay) throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
        ArrayList<PetriP> d_P = new ArrayList<>();
        ArrayList<PetriT> d_T = new ArrayList<>();
        ArrayList<ArcIn> d_In = new ArrayList<>();
        ArrayList<ArcOut> d_Out = new ArrayList<>();
        d_P.add(new PetriP("P1",1));
        d_P.add(new PetriP("P2",0));
        d_P.add(new PetriP("P3",0));
        d_P.add(new PetriP("P4",0));
        d_P.add(new PetriP("signal",0));
        d_P.add(new PetriP("P7",0));
        d_P.add(new PetriP("amount",0));
        d_P.add(new PetriP("P9",1));
        d_P.add(new PetriP("P10",0));
        d_T.add(new PetriT("trylock",lockDelay));
        d_T.get(0).setPriority(1);
        d_T.add(new PetriT("transfer",transferDelay));
        d_T.get(1).setPriority(1);
        d_T.add(new PetriT("notify",1.0));
        d_T.add(new PetriT("wait",1.0));
        d_T.add(new PetriT("continue",1.0));
        d_T.add(new PetriT("unlock",1.0));
        d_T.get(5).setPriority(1);
        d_In.add(new ArcIn(d_P.get(0),d_T.get(0),1));
        d_In.add(new ArcIn(d_P.get(1),d_T.get(1),1));
        d_In.add(new ArcIn(d_P.get(2),d_T.get(2),1));
        d_In.add(new ArcIn(d_P.get(5),d_T.get(4),1));
        d_In.add(new ArcIn(d_P.get(1),d_T.get(3),1));
        d_In.add(new ArcIn(d_P.get(4),d_T.get(4),1));
        d_In.add(new ArcIn(d_P.get(3),d_T.get(5),1));
        d_In.add(new ArcIn(d_P.get(7),d_T.get(0),1));
        d_In.add(new ArcIn(d_P.get(8),d_T.get(5),1));
        d_Out.add(new ArcOut(d_T.get(0),d_P.get(1),1));
        d_Out.add(new ArcOut(d_T.get(1),d_P.get(2),1));
        d_Out.add(new ArcOut(d_T.get(2),d_P.get(3),1));
        d_Out.add(new ArcOut(d_T.get(2),d_P.get(4),1));
        d_Out.add(new ArcOut(d_T.get(3),d_P.get(5),1));
        d_Out.add(new ArcOut(d_T.get(4),d_P.get(1),1));
        d_Out.add(new ArcOut(d_T.get(1),d_P.get(6),1));
        d_Out.add(new ArcOut(d_T.get(5),d_P.get(7),1));
        d_Out.add(new ArcOut(d_T.get(0),d_P.get(8),1));
        PetriNet d_Net = new PetriNet("Untitled",d_P,d_T,d_In,d_Out);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return d_Net;
    }
    public static PetriNet CreateUserNet(int amountOfTransactions) throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
        ArrayList<PetriP> d_P = new ArrayList<>();
        ArrayList<PetriT> d_T = new ArrayList<>();
        ArrayList<ArcIn> d_In = new ArrayList<>();
        ArrayList<ArcOut> d_Out = new ArrayList<>();
        d_P.add(new PetriP("P1",amountOfTransactions));
        d_P.add(new PetriP("P2",1));
        d_T.add(new PetriT("T1",1.0));
        d_In.add(new ArcIn(d_P.get(0),d_T.get(0),1));
        d_Out.add(new ArcOut(d_T.get(0),d_P.get(1),1));
        PetriNet d_Net = new PetriNet("Untitled",d_P,d_T,d_In,d_Out);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return d_Net;
    }

}


