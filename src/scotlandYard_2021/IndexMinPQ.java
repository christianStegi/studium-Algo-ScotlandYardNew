package shortestPath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * Prioritätsliste mit Elementen bestehend aus Schlüssel und Prioritätswert.
 * Liste ist geordnet nach den Priotätswerten (Heap Struktur).
 * Prioritätswerte können effizient in O(log n) geändert werden.
 * Beachte Unterschied zur Java PriorityList, wo Prioritätswerte nur mit O(n) geändert werden können.
 * 
 * Operation add, change, remove, removeMin in O(log n).
 * Operation get, getMinKey, getMinValue in O(1) (Hashtabelle bzw. indizierter Zugriff).
 * 
 * Siehe auch IndexMinPQ in Sedgewick, Algorthms, 4.ed., 2012, Seite 320. 
 * 
 * @author Oliver Bittel
 * @param <Key>	Schlüsseltyp. Vorsicht: hashCode muss geeignet überschrieben sein.
 * @param <PrioValue> Prioritätswerte. PrioValue muss vom Subtpy Comparable<PrioValue> sein (Prüfung zur Laufzeit).
 * @since 21.3.2021
 */
public class IndexMinPQ<Key, PrioValue> {
	private static final int DEFAULT_SIZE = 1024;
	private Key[] heap;
	private final HashMap<Key,PrioPos<PrioValue>> prioPos;
	private int size;
	private final Comparator<PrioValue> cmp;
	
	private static class PrioPos<V> {
		V prio;
		int pos;

		public PrioPos(V prio, int pos) {
			this.prio = prio;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "PrioPos{" + "prio=" + prio + ", pos=" + pos + '}';
		}
	}

	/**
	 * Konstruktor.
	 */
	@SuppressWarnings({"unchecked"})
	public IndexMinPQ() {
		heap = (Key[]) new Object[DEFAULT_SIZE];
		prioPos = new HashMap<>();
		size = 0;
		// Natural Order als Default-Wert:
		cmp = (x,y) -> ((Comparable<? super PrioValue>) x).compareTo(y);	
	}
	
	/**
	 * Löscht die Prioritätsliste.
	 */
	@SuppressWarnings("unchecked")
	public final void clear() {
		heap = (Key[]) new Object[DEFAULT_SIZE];
		prioPos.clear();
		size = 0;
	}
	
	/**
	 * Liefert die Anzahl der Elemente in der Prioritätsliste zurück.
	 * @return Abzahl der Elemente.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Prüft, ob die Prioritätsliste leer ist.
	 * @return true, falls die Prioritätsliste leer ist.
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * Fügt Schlüssel und Prioritätswert ein.
	 * @param k Schlüssel 
	 * @param v Prioritätswert
	 * @return true, falls Schlüssel frisch eingefügt wurde.
	 */
	public boolean add(Key k, PrioValue v) {
		if (prioPos.containsKey(k))
			return false; // key bereits vorhanden.
		
		if (size == heap.length)
			heap = Arrays.copyOf(heap, 2*size);
		heap[size] = k;			
		// prio[k] = x und pos[k] = size;
		prioPos.put(k, new PrioPos<>(v,size));
		size++;
		upheap(size - 1);
		return true;
	}
	
	/**
	 * Ändert den Prioritätswert für Schlüssel k.
	 * @param k Schlüssel
	 * @param v neuer Prioritätswert
	 * @return alter Prioritätswert oder null, falls Schlüssel k nicht vorhanden ist.
	 */
	public PrioValue change(Key k, PrioValue v) {
		if (!prioPos.containsKey(k))
			return null; // key nicht vorhanden.	
		PrioValue oldPrioValue = prioPos.get(k).prio;
		prioPos.get(k).prio = v;
		int i = prioPos.get(k).pos;
		upheap(i);
		downheap(i);
		return oldPrioValue;
	}
	
	/**
	 * Gibt den Prioritätswert für Schlüssel k zurück.
	 * @param k Schlüssel.
	 * @return Prioritätswert oder null, falls Schlüssel k nicht vorhanden ist.
	 */
	public PrioValue get(Key k) {
		PrioPos<PrioValue> pp = prioPos.get(k);
		if (pp == null)
			return null;
		else
			return pp.prio;
	}

	/**
	 * Löscht Schlüssel und Prioritätswert.
	 * @param k Schlüssel.
	 * @return Alter Prioritätswert oder null, falls Schlüssel k nicht vorhanden ist.
	 */
	public PrioValue remove(Key k) {
		if (!prioPos.containsKey(k))
			return null; // key nicht vorhanden.	
		//int remPos = pos[k];
		int remPos = prioPos.get(k).pos;
		PrioValue oldPrioValue = prioPos.get(k).prio;
		//pos[k] = -1;
		prioPos.remove(k);
		heap[remPos] = heap[size - 1];
		heap[size - 1] = null;
		size--;
		//System.out.println("prioPos.get(heap[remPos]) = " + prioPos.get(heap[remPos]));
		//pos[heap[remPos]] = remPos;
		if (remPos != size) {
			prioPos.get(heap[remPos]).pos = remPos; 
			upheap(remPos);
			downheap(remPos);
		}
		return oldPrioValue;
	}
	
	
	/**
	 * Löscht kleinsten Prioritätswert mit Schlüssel.
	 * @return Gelöschter Schlüssel oder null, falls Prioritätsliste leer ist.
	 */
	public Key removeMin() {
		if (size == 0)
			return null;
		Key k = heap[0];
		heap[0] = heap[size - 1];
		heap[size - 1] = null;
		size--;
		if (size > 0) {
			prioPos.get(heap[0]).pos = 0; 
			downheap(0);
		}
		return k;
	}
	
	/**
	 * Liefert Schlüssel mit kleinstem Prioritätswert.
	 * @return Schlüssel mit kleinstem Prioritätswert oder null, falls Prioritätsliste leer ist.
	 */
	public Key getMinKey() {
		if (size == 0)
			return null;
		return heap[0];
	}
	
	/**
	 * Liefert kleinsten Prioritätswert.
	 * @return kleinster Prioritätswert oder null, falls Prioritätsliste leer ist.
	 */
	public PrioValue getMinValue() {
		if (size == 0)
			return null;
		return prioPos.get(heap[0]).prio;
	}
	
	private void upheap(int i) {
		Key key = heap[i];
		// while (i > 0 && prio[heap[(i-1)/2]] < prio[key])
		while (i > 0 && cmp.compare(prioPos.get(heap[(i-1)/2]).prio, prioPos.get(key).prio) > 0) {
			heap[i] = heap[(i - 1) / 2];
			// pos[heap[i]] = i; 
			prioPos.get(heap[i]).pos = i;
			i = (i - 1) / 2;
		}
		heap[i] = key;
		// pos[key] = i;
		prioPos.get(key).pos = i; 
	}
	
	private void downheap(int i) {
		Key key = heap[i];
		int j;

		// Im unteren Teil des Heap-Pfades einfuegen (analog zu Sortien durch Einfuegen):
		while (2 * i + 1 < size) // heap[i] hat linkes Kind
		{
			j = 2 * i + 1; // heap[j] ist linkes Kind von heap[i]
			if (j + 1 < size) { // heap[i] hat rechtes Kind
				// if (data[heap[j+1]] < data[heap[j]]) {
				if (cmp.compare(prioPos.get(heap[j+1]).prio, prioPos.get(heap[j]).prio) < 0)
					j++;
			}
			// heap[j] ist jetzt das kleinste Kind
			// if (data[key] <= data[heap[j]]) {
			if (cmp.compare(prioPos.get(key).prio, prioPos.get(heap[j]).prio) <= 0) {
				break;
			}
			// Schiebe unteres Element a[j] um eine Pos. nach oben:	 	
			heap[i] = heap[j];
			// pos[heap[i]] = i;
			prioPos.get(heap[i]).pos = i;
			i = j;
		}
		heap[i] = key;
		// pos[key] = i;
		prioPos.get(key).pos = i; 
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++)
			sb.append("(").append(heap[i]).append(",").append(prioPos.get(heap[i]).prio).append("),");
		sb.append(" size = ").append(size);
		//sb.append(prioPos);
		return sb.toString();
	}
	
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		IndexMinPQ<String, Integer> pq = new IndexMinPQ<>();
		
		pq.add("abc", 5);
		pq.add("abc", 7); // wird ignoriert
		pq.add("def", 3); 
		pq.add("ghi", 8); 
		pq.add("jkl", 2);
		pq.add("xyz", 9);
		pq.change("xyz", 1);
		pq.add("uvw", 1);
		
		System.out.println(pq.size()); // 6
		
		// "xyz": 1 // Reihenfolge von "xyz" und "uvw" ist nicht eindeutig, da beide PrioWerte = 1.
		// "uvw": 1 
		// "jkl": 2
		// "def": 3
		// "abc": 5
		// "ghi": 8
		while (!pq.isEmpty()) {
			System.out.println(pq.getMinKey() + ": " + pq.getMinValue()); 
			pq.removeMin(); 
		}
	}
}
