package HelperClasses;
import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }
  //
  public List<Pair<String,String>> path(int id){// most probably be the main cause of the error
    if (rootnode.left == null) {
      ArrayList<Pair<String,String>> V = new ArrayList<Pair<String,String>>();
      Pair<String,String> P = new Pair<String,String>(rootnode.parent.left.val,rootnode.parent.right.val);
      V.add(P);
      return V;
    }
    else{
      if(id < numdocs/2){
        MerkleTree l = new MerkleTree();
        l.rootnode = rootnode.left;
        l.numdocs = (numdocs)/2;
        List<Pair<String,String>> V = l.path(id);
        if(rootnode.parent == null ){
          Pair<String,String> P = new Pair<String,String>(rootnode.val,null);
          V.add(P);
          return V;
        }
        else{
          Pair<String,String> P =
                  new Pair<String,String>(rootnode.parent.left.val,rootnode.parent.right.val);
          V.add(P);
          return V;
        }
      }
      else{
        MerkleTree r = new MerkleTree();
        r.rootnode = rootnode.right;
        r.numdocs = (numdocs)/2;
        List<Pair<String,String>> V = r.path(id - numdocs/2);
        if(rootnode.parent == null ){
          Pair<String,String> P = new Pair<String,String>(rootnode.val,null);
          V.add(P);
          return V;
        }
        else{
          Pair<String,String> P =
                  new Pair<String,String>(rootnode.parent.left.val,rootnode.parent.right.val);
          V.add(P);
          return V;
        }
      }

    }
  }
}
