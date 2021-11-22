package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import jdk.swing.interop.SwingInterOpUtils;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans = new Transaction[100];
  //
  public void mycoinsort(){
   int n = mycoins.size();
   for(int i = 0;i < n-1;i++){
    for(int k = 0;k < n - 1 -i;k++){
     if(Integer.parseInt(mycoins.get(k).first) > Integer.parseInt(mycoins.get(k+1).first) ){
      Pair<String,TransactionBlock> P = mycoins.get(k);
      mycoins.set(k,mycoins.get(k+1));
      mycoins.set(k+1,P);
     }
    }
   }
  }
  //correct
   public void initiateCoinsend(String destUID, DSCoin_Honest DSobj){// not added the next function,what if mycoin == null
    Pair<String, TransactionBlock> P = mycoins.get(0);
    mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = P.first;
    tobj.coinsrc_block = P.second;
    tobj.Source = this;
    Members[] M = DSobj.memberlist;
    int n = M.length;
    int b = -1;
    for (int i = 0; i < n; i++) {
     if (M[i].UID == destUID) {
      b = i;
      break;
     }
    }
    tobj.Destination = M[b];
    if(in_process_trans != null) {
     int k = in_process_trans.length;
     int c = -1;
     for (int i = 0; i < k; i++) {
      if (in_process_trans[i] == null) {
       c = i;
       break;
      }
     }
     in_process_trans[c] = tobj;
    }
    else{
     in_process_trans = new  Transaction[100];
     in_process_trans[0] = tobj;
    }
    DSobj.pendingTransactions.AddTransactions(tobj);
   }
  //CORRECT
   public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
   Pair<String, TransactionBlock> P = mycoins.get(0);
   mycoins.remove(0);
   Transaction tobj = new Transaction();
   tobj.coinID = P.first;
   tobj.coinsrc_block = P.second;
   tobj.Source = this;
   Members[] M = DSobj.memberlist;
   int n = M.length;
   int b = -1;
   for (int i = 0; i < n; i++) {
    if (M[i].UID == destUID) {
     b = i;
     break;
    }
   }
   tobj.Destination = M[b];
   int k = in_process_trans.length;
   int c = -1;
   for (int i = 0; i < k; i++) {
    if (in_process_trans[i] == null) {
     c = i;
     break;
    }
   }
   in_process_trans[c] = tobj;
   DSobj.pendingTransactions.AddTransactions(tobj);
  }

  //a transaction is present in a transaction block or not
  // CORRECT
  public boolean ispresent(TransactionBlock B,Transaction T){
   int n = B.trarray.length;
   for(int i = 0; i < n;i++){
    if(B.trarray[i].Source == null && T.Source == null){
     if(B.trarray[i].Destination.UID.equals(T.Destination.UID) && B.trarray[i].coinID.equals(T.coinID) && B.trarray[i].coinsrc_block == T.coinsrc_block ){//isme coinsrc add karna hein
      return true;
     }
    }
    if(B.trarray[i].Source != null && T.Source != null){
     if(B.trarray[i].Source.UID.equals(T.Source.UID) && B.trarray[i].Destination.UID.equals(T.Destination.UID) &&
             B.trarray[i].coinID.equals(T.coinID) && B.trarray[i].coinsrc_block == T.coinsrc_block ){ // isme coinsrc add karna hein
      return true;
     }
    }
   }
   return false;
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>>
  finalizeCoinsend(Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
   // finding transaction block
   BlockChain_Honest Bc = DSObj.bChain;
   TransactionBlock TB = Bc.lastBlock;
   TransactionBlock contain = null;
   if (TB == null) {
    System.out.println(tobj.Source.UID + " sends the coin " + tobj.coinID + " to " + tobj.Destination.UID);
    throw new MissingTransactionException();
   }
   int a = 0;
   while (TB != null) {
    if (ispresent(TB, tobj) == true) {
     contain = TB;
     a = 1;
     break;
    }
    TB = TB.previous;
   }
   if (a == 0) {
    System.out.println(tobj.Source.UID + " sends the coin " + tobj.coinID + " to " + tobj.Destination.UID);
    throw new MissingTransactionException();
   }
   // found
   // now computing sibling coupled path to root
   int id = -1;
   int j = contain.trarray.length;
   for (int i = 0; i < j; i++) {
    if (contain.trarray[i].Source == null && tobj.Source == null) {
     if (contain.trarray[i].Destination.UID.equals(tobj.Destination.UID) &&
             contain.trarray[i].coinID.equals(tobj.coinID) && contain.trarray[i].coinsrc_block == tobj.coinsrc_block) {
      id = i;
      break;
     }
    }
    if (contain.trarray[i].Source != null && tobj.Source != null) {
     if (contain.trarray[i].Source.UID.equals(tobj.Source.UID) && contain.trarray[i].Destination.UID.equals(tobj.Destination.UID) &&
             contain.trarray[i].coinID.equals(tobj.coinID) && contain.trarray[i].coinsrc_block == tobj.coinsrc_block) {
      id = i;
      break;
     }
    }
   }
   //
   List<Pair<String, String>> Flist = contain.Tree.path(id);
   // computed
   //computing second list
   List<Pair<String, String>> Slist = new ArrayList<Pair<String, String>>();
   List<Pair<String, String>> reverse = new ArrayList<Pair<String, String>>();
   TransactionBlock e = Bc.lastBlock;
   while (e != contain) {
    Pair<String, String> P = new Pair<String, String>
            (e.dgst, e.previous.dgst + "#" + e.trsummary + "#" + e.nonce);
    reverse.add(P);
    e = e.previous;
   }
   if (contain.previous == null) {
    Pair<String, String> P1 = new Pair<String, String>
            (contain.dgst, "DSCoin" + "#" + contain.trsummary + "#" + contain.nonce);// might be wrong
    reverse.add(P1);
    Pair<String, String> P2 = new Pair<String, String>("DSCoin", null);
    reverse.add(P2);
   } else {
    Pair<String, String> P3 = new Pair<String, String>
            (contain.dgst, contain.previous.dgst + "#" + contain.trsummary + "#" + contain.nonce);// might be wrong
    reverse.add(P3);
    Pair<String, String> P4 = new Pair<String, String>(contain.previous.dgst, null);
    reverse.add(P4);
   }
   int z = reverse.size();
   for (int i = 0; i < z; i = i + 1) {
    Slist.add(reverse.get(z - i - 1));
   }
   // computed
   int num = in_process_trans.length;
   int pos = -1;
   for (int i = 0; i < num; i++) {
    if (in_process_trans[i] != null) {
     if (in_process_trans[i].Source == null && tobj.Source == null) {
      if (in_process_trans[i].Destination.UID.equals(tobj.Destination.UID) &&
              in_process_trans[i].coinID.equals(tobj.coinID) && in_process_trans[i].coinsrc_block == tobj.coinsrc_block) {//isme coinsrc add karna hein
       pos = i;
      }
     }
     if (in_process_trans[i].Source != null && tobj.Source != null) {
      if (in_process_trans[i].Source.UID.equals(tobj.Source.UID) && in_process_trans[i].Destination.UID.equals(tobj.Destination.UID) &&
              in_process_trans[i].coinID.equals(tobj.coinID) && in_process_trans[i].coinsrc_block == tobj.coinsrc_block ) { // isme coinsrc add karna hein
       pos = i;
      }
     }
    }
   }
   if (pos >= 0) {
    in_process_trans[pos] = null;
    if (pos != num - 1) {
     for (int i = pos; i <= num - 2; i++) {
      in_process_trans[i] = in_process_trans[i + 1];
     }
     in_process_trans[num - 1] = null;
    }
   }
    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tobj.coinID, contain);
    tobj.Destination.mycoins.add(coin);
    tobj.Destination.mycoinsort();
    Pair<List<Pair<String, String>>, List<Pair<String, String>>> Q = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(Flist, Slist);
    return Q;
  }
  //
  public boolean checkTransaction(Transaction t,BlockChain_Malicious bchain) {
   if(t.coinsrc_block == null ){// this is a little tricky and might be wrong, but includes reward coins and moderater given coin
    return true;
   }
   //checking after the moderater has inserted
   TransactionBlock Tb = bchain.FindLongestValidChain();
   while (Tb != null) {
    //originsite
    if (Tb == t.coinsrc_block) {
     int n = Tb.trarray.length;
     for (int i = 0; i < n; i++) {
      if (Tb.trarray[i].coinID.equals(t.coinID) && Tb.trarray[i].Destination == t.Source) {
       return true;
      }
     }
     return false;
     //checked
    }
    // not origin site
    else {
     int m = Tb.trarray.length;
     for (int i = 0; i < m; i++) {
      if (Tb.trarray[i].coinID.equals( t.coinID)) {
       return false;
      }
     }
    }
    Tb = Tb.previous;
   }
   return false;
  }
  //
  public boolean coin_notpresent(Transaction[] arr,String id){
   int n = arr.length;
   for(int i = 0; i < n;i++){
    if(arr[i] != null) {
     if (arr[i].coinID.equals(id)) {
      return false;
     }
    }
   }
   return true;
  }
  //
  //
  public boolean checkTransaction(Transaction t,BlockChain_Honest bchain){
   if(t.coinsrc_block == null ){// this is a little tricky and might be wrong, but includes reward coins and moderater given coin
    return true;
   }
   //checking after the moderater has inserted
   TransactionBlock Tb = bchain.lastBlock;
   while (Tb != null) {
    //originsite
    if (Tb == t.coinsrc_block) {
     int n = Tb.trarray.length;
     for (int i = 0; i < n; i++) {
      if (Tb.trarray[i].coinID.equals(t.coinID) && Tb.trarray[i].Destination == t.Source) {
       return true;
      }
     }
     return false;
     //checked
    }
    // not origin site
    else {
     int m = Tb.trarray.length;
     for (int i = 0; i < m; i++) {
      if (Tb.trarray[i].coinID.equals( t.coinID)) {
       return false;
      }
     }
    }
    Tb = Tb.previous;
   }
   return false;
  }
  //
  public void MineCoin(DSCoin_Honest DSObj) {//assuming there are tr_count - 1 transactions
   int n = DSObj.bChain.tr_count - 1;
   Transaction T = DSObj.pendingTransactions.firstTransaction;
   Transaction[] trarray1 = new Transaction[n + 1];
   int i = 0;
   while (i != 1) {
    if (checkTransaction(T, DSObj.bChain) == true) {
     trarray1[0] = T;
     i = 1;
     try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
    }
    else {
     try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
     T = DSObj.pendingTransactions.firstTransaction;
    }
   }

   T = DSObj.pendingTransactions.firstTransaction;
   while (i != n ) {
     if (checkTransaction(T, DSObj.bChain) == true) {
      if(coin_notpresent(trarray1,T.coinID) == true) {
       trarray1[i] = T;
       i = i + 1;
       try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
       T = DSObj.pendingTransactions.firstTransaction;
      }
      else{
       try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
       T = DSObj.pendingTransactions.firstTransaction;
      }
     }
     else {
      try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
      T = DSObj.pendingTransactions.firstTransaction;
     }
    }
   T = DSObj.pendingTransactions.firstTransaction;
   Transaction reward = new Transaction();
   reward.Source   = null;
   reward.coinsrc_block = null;
   reward.next = null;
   reward.Destination = this;
   reward.coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID)+1); //doubtful
   DSObj.latestCoinID = reward.coinID;
   trarray1[n] = reward;
   // now awarding coin to miner
   TransactionBlock tB = new TransactionBlock(trarray1);
   DSObj.bChain.InsertBlock_Honest(tB);
   Pair<String,TransactionBlock> P = new Pair<String,TransactionBlock>(reward.coinID,tB);
   this.mycoins.add(P);
   this.mycoinsort();
   }

  //
  public void MineCoin(DSCoin_Malicious DSObj) {
   int n = DSObj.bChain.tr_count - 1;
   Transaction T = DSObj.pendingTransactions.firstTransaction;
   Transaction[] trarray1 = new Transaction[n + 1];
   int i = 0;
   while (i != 1) {
    if (checkTransaction(T, DSObj.bChain) == true) {
     trarray1[0] = T;
     i = 1;
     try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
    }
    else {
     try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
     T = DSObj.pendingTransactions.firstTransaction;
    }
   }
   T = DSObj.pendingTransactions.firstTransaction;
   while (i != n ) {
    if (checkTransaction(T, DSObj.bChain) == true) {
     if(coin_notpresent(trarray1,T.coinID) == true) {
      trarray1[i] = T;
      i = i + 1;
      try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
      T = DSObj.pendingTransactions.firstTransaction;
     }
     else{
      try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
      T = DSObj.pendingTransactions.firstTransaction;
     }
    }
    else {
     try {DSObj.pendingTransactions.RemoveTransaction();} catch (Exception e) {}
     T = DSObj.pendingTransactions.firstTransaction;
    }
   }
   T = DSObj.pendingTransactions.firstTransaction;
   Transaction reward = new Transaction();
   reward.Source   = null;
   reward.coinsrc_block = null;
   reward.next = null;
   reward.Destination = this;
   reward.coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID)+1); //doubtful
   DSObj.latestCoinID = reward.coinID;
   trarray1[n] = reward;
   // now awarding coin to miner
   TransactionBlock tB = new TransactionBlock(trarray1);
   DSObj.bChain.InsertBlock_Malicious(tB);
   Pair<String,TransactionBlock> P = new Pair<String,TransactionBlock>(reward.coinID,tB);
   this.mycoins.add(P);
   this.mycoinsort();
  }  
}
















