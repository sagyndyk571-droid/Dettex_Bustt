package zov.viola.util.commands.api.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Registry<V> {
   private final Deque<V> rtpIvyK = new LinkedList<>();
   private final Set<V> x810R = new HashSet<>();
   public final Collection<V> entries = Collections.unmodifiableCollection(this.rtpIvyK);

   public boolean registered(V entry) {
      return this.x810R.contains(entry);
   }

   public void register(V entry) {
      if (!this.registered(entry)) {
         this.rtpIvyK.addFirst(entry);
         this.x810R.add(entry);
      }
   }

   public void unregister(V entry) {
      if (this.registered(entry)) {
         this.rtpIvyK.remove(entry);
         this.x810R.remove(entry);
      }
   }

   public Iterator<V> iterator() {
      return this.rtpIvyK.iterator();
   }

   public Iterator<V> descendingIterator() {
      return this.rtpIvyK.descendingIterator();
   }

   public Stream<V> stream() {
      return this.rtpIvyK.stream();
   }

   public Stream<V> descendingStream() {
      Spliterator<V> spliterator = Spliterators.spliterator(this.descendingIterator(), (long)this.rtpIvyK.size(), 16448);
      return StreamSupport.stream(spliterator, false);
   }
}
