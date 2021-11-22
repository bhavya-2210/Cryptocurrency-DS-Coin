package DSCoinPackage;

import HelperClasses.CRF;
//CORRECT
public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;
  // CORRECT
  public String Nonce(String a,String b){
    CRF obj = new CRF(64);
    long c = 1000000001L;
    while(c <= 9999999999L){
      String i =  String.valueOf(c);
      String k = obj.Fn(a + "#" + b + "#" + i);
      if(k.substring(0,4).equals("0000")){
        return i;
      }
      else{
        c = c + 1;
      }
    }
    return null;
  }
  // CORRECT
  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    if(lastBlock == null){
      newBlock.nonce = Nonce(start_string,newBlock.trsummary);
      newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      lastBlock = newBlock;
    }
    else {
      newBlock.nonce = Nonce(lastBlock.dgst,newBlock.trsummary);
      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
    }
  }
}
