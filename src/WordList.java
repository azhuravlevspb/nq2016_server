import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anton on 12.01.2016.
 */
public class WordList {
    List<Word> list = new ArrayList<>();
    public void add(Word word){
        list.add(word);
        list = list.stream().filter(w -> w.time.isAfter(Clock.systemDefaultZone().instant())).collect(Collectors.toCollection(ArrayList<Word>::new));
    }
    public Word get(String hash){
        for(Word w : list){
            if(w.hash.equals(hash)){
                return w;
            }
        }
        return null;
    }
}
