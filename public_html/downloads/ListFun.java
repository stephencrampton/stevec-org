import java.util.*;
import java.io.*;
import static java.lang.System.*;

public class ListFun
{
    private List<String> items;
    
    public ListFun()
    {
        items = new LinkedList<String>();
    }
    
    public void fillList()
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader("/Users/guest474/insults.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                items.add(str + "\n");
            }
            in.close();
        }
        catch (IOException e)
        {
            out.println("Could not read from the insults.txt file.");
            exit(-1);
        }
    }
    
    public String toString()
    {
        return items.toString();
    }
    
    public void printIt()
    {
        Iterator<String> itr = items.iterator();
        while (itr.hasNext())
        {
            out.print(itr.next());
        }
    }
    
    public void removeSomething()
    {
        Scanner kb = new Scanner(in);
        out.print("Please enter search text: ");
        String val = kb.next();
        Iterator<String> itr = items.iterator();
        while (itr.hasNext())
        {
            String str = itr.next();
            if (!str.contains(val))
            {
                itr.remove();
            }
        }
    }
    
    public void padList()
    {
        ListIterator<String> itr = items.listIterator();
        while (itr.hasNext())
        {
            String s = itr.next();
            if (itr.hasNext())
            {
                itr.add("\n");
            }
        }
    }
    
    public static void main(String[] args)
    {
        ListFun funny = new ListFun();
        funny.fillList();
        funny.removeSomething();
        funny.printIt();
    }
}
