package DSCoinPackage;
import DSCoinPackage.*;
import HelperClasses.CRF;
import HelperClasses.MerkleTree;
import HelperClasses.Pair;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList = new TransactionBlock[100];

  public String Nonce(String a, String b) {
    CRF obj = new CRF(64);
    long c = 1000000001L;
    while (c <= 9999999999L) {
      String i = String.valueOf(c);
      String k = obj.Fn(a + "#" + b + "#" + i);
      if (k.substring(0, 4).equals("0000")) {
        return i;
      } else {
        c = c + 1L;
      }
    }
    return null;
  }

  public static boolean checkTransactionBlock(TransactionBlock tB) {
    CRF obj = new CRF(64);
    if (!tB.dgst.substring(0, 4).equals("0000")) {
      return false;
    }
    if (tB.previous == null) {
      if (!tB.dgst.equals(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce))) {
        return false;
      }
    }
    if (tB.previous != null) {
      if (!tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce))) {
        return false;
      }
    }
    MerkleTree M = new MerkleTree();
    M.Build(tB.trarray);
    if (!tB.trsummary.equals(M.rootnode.val)) {
      return false;
    }
    int n = tB.trarray.length;
    for (int i = 0; i < n; i++) {
      if (tB.checkTransaction(tB.trarray[i]) == false) {
        return false;
      }
    }
    return true;
  }

  public Pair<Integer, TransactionBlock> chainlength(TransactionBlock T) {
    if (T.previous == null) {
      if (checkTransactionBlock(T) == false) {
        Pair<Integer, TransactionBlock> P = new Pair<Integer,TransactionBlock>(0, null);
        return P;
      }
      else {
        Pair<Integer, TransactionBlock> P = new Pair<Integer,TransactionBlock>(1, T);
        return P;
      }
    }
    else {
      if (checkTransactionBlock(T) == false) {
        return chainlength(T.previous);
      }
      else {
        if (T.previous.previous == null) {
          if (chainlength(T.previous).first == 1) {
            Pair<Integer, TransactionBlock> P = new Pair<Integer, TransactionBlock>(2, T);
            return P;
          } else {
            Pair<Integer, TransactionBlock> P = new Pair<Integer, TransactionBlock>(0, null);
            return P;
          }
        }
        else {
          if (chainlength(T.previous).second == T.previous) {// condition of T.previous.previous == null not considered
            Pair<Integer, TransactionBlock> P = new Pair<Integer,TransactionBlock>(1 + chainlength(T.previous).first, T);
            return P;
          }
          else {
            return chainlength(T.previous);
          }
        }
      }
    }
  }

  public TransactionBlock FindLongestValidChain() {
    int n = 0;
    int k = lastBlocksList.length;
    for (int i = 0; i < k; i++) {
      if (lastBlocksList[i] == null) {
        n = i;
        break;
      }
    }
    if (n == 0) {
      return null;
    }
    else {

      TransactionBlock T = lastBlocksList[0];
      int m = chainlength(T).first;
      for (int i = 1; i < n; i++) {
        int p = chainlength(lastBlocksList[i]).first;
        if (p > m) {
          m = p;
          T = lastBlocksList[i];
        }
      }
      return chainlength(T).second;
    }
  }

  public void InsertBlock_Malicious(TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    if (this.FindLongestValidChain() == null) {
      String s = Nonce(start_string, newBlock.trsummary);
      newBlock.nonce = s;
      newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      newBlock.previous = null;
      lastBlocksList[0] = newBlock;
    }
    else {
      TransactionBlock lastBlock = this.FindLongestValidChain();
      String s = Nonce(lastBlock.dgst, newBlock.trsummary);
      newBlock.nonce = s;
      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
      newBlock.previous = lastBlock;
      int n = 0;
      int k = lastBlocksList.length;
      for (int i = 0; i < k; i++) {
        if (lastBlocksList[i] == null) {
          n = i;
          break;
        }
      }
      int a = -10;
      for (int i = 0; i < n; i++) {
        if (lastBlock == lastBlocksList[i]) {
          a = i;
        }
      }
      if (a == -10) {
        lastBlocksList[n] = newBlock;
      } else {
        lastBlocksList[a] = newBlock;
      }
    }
  }
}