package DSCoinPackage;

import HelperClasses.Pair;

import java.util.Arrays;

public class Moderator {
 public void assigncoin(TransactionBlock Tb) {
  int n = Tb.trarray.length;
  for (int i = 0; i < n; i++) {
   Pair<String, TransactionBlock> P = new Pair<String, TransactionBlock>(Tb.trarray[i].coinID, Tb);
   Tb.trarray[i].Destination.mycoins.add(P);
  }
 }

 //
 //
 public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
  Members m = new Members();
  m.UID = "Moderator";
  Transaction[] Q = new Transaction[coinCount];
  int cid = 100000;
  int n = DSObj.memberlist.length;
  int a = coinCount;
  for (int i = 0; i < a; i++) {
   Transaction T = new Transaction();
   T.coinID = String.valueOf(cid);
   T.coinsrc_block = null;
   T.Destination = DSObj.memberlist[i % n];
   T.Source = m;
   T.next = null;
   Q[i] = T;
   cid = cid + 1;
  }
  int g = DSObj.bChain.tr_count;
  for (int i = 0; i < coinCount / g; i++) {
   TransactionBlock Tb = new TransactionBlock(Arrays.copyOfRange(Q, i * g, (i + 1) * g));
   DSObj.bChain.InsertBlock_Honest(Tb);
   assigncoin(DSObj.bChain.lastBlock);
  }
  DSObj.latestCoinID = String.valueOf(cid - 1);
 }

 public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
  Members m = new Members();
  m.UID = "Moderator";
  Transaction[] Q = new Transaction[coinCount];
  int cid = 100000;
  int n = DSObj.memberlist.length;
  int a = coinCount;
  for (int i = 0; i < a; i++) {
   Transaction T = new Transaction();
   T.coinID = String.valueOf(cid);
   T.coinsrc_block = null;
   T.Destination = DSObj.memberlist[i % n];
   T.Source = m;
   T.next = null;
   Q[i] = T;
   cid = cid + 1;
  }
  int g = DSObj.bChain.tr_count;
  for (int i = 0; i < coinCount / g; i++) {
   TransactionBlock Tb = new TransactionBlock(Arrays.copyOfRange(Q, i * g, (i + 1) * g));
   DSObj.bChain.InsertBlock_Malicious(Tb);
   assigncoin(DSObj.bChain.FindLongestValidChain());
  }
  DSObj.latestCoinID = String.valueOf(cid - 1);
 }
}
