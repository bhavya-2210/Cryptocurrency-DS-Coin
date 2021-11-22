package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;
  TransactionBlock(Transaction[] t) {
    int n = t.length;
    Transaction[] a = new Transaction[n];
    for(int i = 0;i < n;i++){
      a[i] = t[i];
    }
    trarray = a;
    previous = null;
    dgst = null;
    nonce = null;
    MerkleTree T = new MerkleTree();
    T.Build(a);
    T.numdocs = n;
    Tree = T;
    trsummary = Tree.rootnode.val;// maybe veryfying trsummary might create problems

  }
  public boolean checkTransaction (Transaction t) {
    TransactionBlock Tb = this;
    if(t.coinsrc_block == null ){
      return true;
    }
    if (this.previous == null) {
      return false;
    }
    Tb = Tb.previous;
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
}