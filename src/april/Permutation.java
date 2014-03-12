/*
 * Class is copied from April library (licensed under GNU GPL)
 * http://april.eecs.umich.edu/wiki/index.php/Vis
 */

package april;
/** The output of a variable reordering algorithm. **/
public class Permutation
{
    public int perm[];
    public int invperm[];

    public Permutation(int perm[])
    {
        this.perm = perm;

        invperm = new int[perm.length];
        for (int i = 0; i < perm.length; i++)
            invperm[perm[i]] = i;
    }
}