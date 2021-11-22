package DSCoinPackage;

// CORRECT
public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions(Transaction transaction) {
    if(numTransactions == 0 ){
      transaction.next = null;
      this.firstTransaction = transaction;
      this.lastTransaction = transaction;
      numTransactions = 1;
    }
    else{
      transaction.next = this.lastTransaction;
      this.lastTransaction = transaction;// changed at night
      numTransactions += 1;
    }
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(numTransactions == 0){
      throw new EmptyQueueException();
    }
    if(numTransactions == 1){
      Transaction Tran = firstTransaction;
      firstTransaction = null;
      lastTransaction = null;
      numTransactions = 0;
      return Tran;
    }
    else {
       Transaction Tran = firstTransaction;
       Transaction I = lastTransaction;
       while (I.next != firstTransaction) {
        I = I.next;
      }
      firstTransaction = I;
      firstTransaction.next = null;
      numTransactions = numTransactions -1;
      return Tran;
    }
  }

  public int size() {
    return numTransactions;
  }
}
