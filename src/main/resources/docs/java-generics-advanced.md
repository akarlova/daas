# Java Generics: от основ до продвинутых техник

## Зачем нужны Generics

До появления дженериков в Java 5 коллекции работали с типом `Object`. Это означало, что в один и тот же список можно было положить строку, число и вообще что угодно — компилятор не мог это проверить. Ошибки обнаруживались только во время выполнения программы:

```java
// До дженериков — опасный код
List names = new ArrayList();
names.add("Alice");
names.add(42); // Компилятор не ругается!

String name = (String) names.get(1); // ClassCastException во время выполнения
```

Generics решают эту проблему, перенося проверку типов на этап компиляции:

```java
// С дженериками — безопасный код
List<String> names = new ArrayList<>();
names.add("Alice");
names.add(42); // Ошибка компиляции! Проблема поймана сразу
```

Главная идея: **параметризовать типы**, чтобы писать универсальный код, который при этом остаётся типобезопасным.

---

## 1. Обобщённые классы

Обобщённый класс объявляет один или несколько параметров типа в угловых скобках:

```java
public class Box<T> {
    private T content;

    public void put(T item) {
        this.content = item;
    }

    public T get() {
        return content;
    }
}
```

При использовании мы подставляем конкретный тип:

```java
Box<String> stringBox = new Box<>();
stringBox.put("Hello");
String value = stringBox.get(); // Кастинг не нужен

Box<Integer> intBox = new Box<>();
intBox.put(123);
Integer number = intBox.get();
```

Можно использовать несколько параметров типа:

```java
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

Pair<String, Integer> entry = new Pair<>("age", 25);
```

### Соглашения по именованию параметров типа

Имена параметров — это одна заглавная буква. Вот общепринятые:

- `T` — Type (общий тип)
- `E` — Element (элемент коллекции)
- `K` — Key (ключ)
- `V` — Value (значение)
- `N` — Number (число)
- `S`, `U` — второй, третий тип (когда T уже занят)

---

## 2. Обобщённые интерфейсы

Интерфейсы тоже могут быть обобщёнными:

```java
public interface Repository<T> {
    void save(T entity);
    T findById(long id);
    List<T> findAll();
}

public class UserRepository implements Repository<User> {
    @Override
    public void save(User entity) { /* ... */ }

    @Override
    public User findById(long id) { /* ... */ }

    @Override
    public List<User> findAll() { /* ... */ }
}
```

Можно и не фиксировать тип при реализации, а пробросить его дальше:

```java
public class GenericRepository<T> implements Repository<T> {
    // T остаётся параметром — конкретный тип определится позже
}
```

---

## 3. Обобщённые методы

Метод может иметь собственные параметры типа, независимые от класса:

```java
public class Utils {

    // <T> перед возвращаемым типом — объявление параметра метода
    public static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }
}

// Использование — компилятор выводит тип автоматически
List<String> names = Utils.listOf("Alice", "Bob", "Carol");
List<Integer> numbers = Utils.listOf(1, 2, 3);

// Можно указать тип явно (редко нужно)
List<String> names = Utils.<String>listOf("Alice", "Bob");
```

Обобщённый метод может быть и в необобщённом классе, и в обобщённом. Его параметры типа живут отдельно от параметров класса:

```java
public class Box<T> {
    private T content;

    // U — параметр именно этого метода, не связан с T
    public <U> Pair<T, U> pairWith(U other) {
        return new Pair<>(content, other);
    }
}
```

---

## 4. Bounded Type Parameters — ограниченные параметры типа

### 4.1. Upper bound — `extends`

Ключевое слово `extends` ограничивает параметр типа сверху: допускаются только подтипы указанного класса или интерфейса.

```java
// T должен быть подтипом Number (Integer, Double, Long и т.д.)
public static <T extends Number> double sum(List<T> numbers) {
    double result = 0;
    for (T number : numbers) {
        result += number.doubleValue(); // Можно вызывать методы Number
    }
    return result;
}

sum(List.of(1, 2, 3));       // OK — Integer extends Number
sum(List.of(1.5, 2.5));      // OK — Double extends Number
sum(List.of("a", "b"));      // Ошибка компиляции! String не extends Number
```

Ограничение даёт нам доступ к методам указанного типа. Без `extends Number` компилятор не знал бы, что у `T` есть метод `doubleValue()`.

### 4.2. Множественные ограничения

Тип может одновременно расширять класс и реализовывать интерфейсы. Класс пишется первым, интерфейсы — через `&`:

```java
// T должен наследовать Number И реализовывать Comparable
public static <T extends Number & Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}

max(3, 7);     // OK — Integer extends Number & implements Comparable<Integer>
max(1.5, 2.5); // OK — Double тоже подходит
```

Порядок важен: класс всегда первый, интерфейсы после.

```java
// Правильно:
<T extends SomeClass & InterfaceA & InterfaceB>

// Неправильно (не скомпилируется):
<T extends InterfaceA & SomeClass & InterfaceB>
```

---

## 5. Wildcards — подстановочные типы

Wildcards — это знак `?`, который обозначает «неизвестный тип». Их используют, когда конкретный тип не важен или когда нужно выразить отношение подтипирования между обобщёнными типами.

### Проблема, которую решают wildcards

Важный факт: **`List<Dog>` не является подтипом `List<Animal>`**, даже если `Dog extends Animal`. Это называется **инвариантность**.

```java
class Animal {}
class Dog extends Animal {}
class Cat extends Animal {}

List<Dog> dogs = List.of(new Dog(), new Dog());

// Казалось бы, логично, но это ОШИБКА КОМПИЛЯЦИИ:
List<Animal> animals = dogs;

// Почему? Представь, что это было бы разрешено:
animals.add(new Cat()); // Добавили кота в список собак!
// Нарушена типобезопасность
```

Wildcards дают нам контролируемую гибкость.

### 5.1. Unbounded wildcard — `?`

`List<?>` — список чего-то неизвестного. Из него можно читать как `Object`, но нельзя ничего добавлять (кроме `null`):

```java
public static void printAll(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

printAll(List.of("a", "b"));  // OK
printAll(List.of(1, 2, 3));   // OK
printAll(List.of(new Dog())); // OK
```

Когда использовать: когда нужно только читать элементы и работать с ними как с `Object`, или когда вызываешь методы, не зависящие от типа элементов (например, `list.size()`, `list.isEmpty()`).

### 5.2. Upper bounded wildcard — `? extends T`

`List<? extends Animal>` — список каких-то наследников `Animal`. Можно **читать** элементы как `Animal`, но **нельзя добавлять** — компилятор не знает, какой конкретно тип хранится:

```java
public static void feedAll(List<? extends Animal> animals) {
    for (Animal animal : animals) { // Чтение — OK
        animal.eat();
    }
    // animals.add(new Dog()); // ОШИБКА! А вдруг это List<Cat>?
}

List<Dog> dogs = List.of(new Dog(), new Dog());
feedAll(dogs); // OK — Dog extends Animal

List<Cat> cats = List.of(new Cat());
feedAll(cats); // OK — Cat extends Animal
```

Почему нельзя добавлять? Потому что `List<? extends Animal>` может оказаться `List<Dog>`, `List<Cat>` или `List<Animal>`. Если бы мы могли добавить `Dog`, а список на самом деле `List<Cat>`, типобезопасность была бы нарушена.

### 5.3. Lower bounded wildcard — `? super T`

`List<? super Dog>` — список, который точно может принять `Dog`. Это может быть `List<Dog>`, `List<Animal>` или `List<Object>`. Можно **добавлять** `Dog` и его наследников, но **читать** можно только как `Object`:

```java
public static void addDogs(List<? super Dog> list) {
    list.add(new Dog());       // OK — список точно принимает Dog
    list.add(new Puppy());     // OK — Puppy extends Dog

    // list.add(new Animal()); // ОШИБКА! А вдруг это List<Dog>?

    Object item = list.get(0); // Читать можно только как Object
}

List<Animal> animals = new ArrayList<>();
addDogs(animals); // OK — Animal — супертип Dog

List<Object> objects = new ArrayList<>();
addDogs(objects); // OK — Object — супертип Dog
```

---

## 6. Принцип PECS — Producer Extends, Consumer Super

Это мнемоника, придуманная Джошуа Блохом (автором Effective Java), которая помогает выбрать правильный wildcard:

- **Producer Extends**: если структура **отдаёт** (производит) элементы — используй `? extends T`
- **Consumer Super**: если структура **принимает** (потребляет) элементы — используй `? super T`

```java
// Копируем из source (producer) в destination (consumer)
public static <T> void copy(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {       // source — producer, читаем из него
        destination.add(item);    // destination — consumer, пишем в него
    }
}

List<Integer> ints = List.of(1, 2, 3);
List<Number> nums = new ArrayList<>();
copy(ints, nums); // Integer extends Number — всё работает
```

Реальный пример из стандартной библиотеки — `Collections.copy()`:

```java
// Именно так объявлен метод в JDK:
public static <T> void copy(List<? super T> dest, List<? extends T> src)
```

Ещё пример — `Comparable` и `Comparator`:

```java
// Плохо — слишком строго:
public static <T extends Comparable<T>> T max(Collection<T> coll)

// Хорошо — гибко:
public static <T extends Comparable<? super T>> T max(Collection<? extends T> coll)
```

Вторая версия работает с `List<Apple>`, даже если `Comparable` реализован в `Fruit` (родителе `Apple`), а не в самом `Apple`.

### Шпаргалка по выбору

| Ситуация | Что использовать |
|---|---|
| Только читаете из коллекции | `? extends T` |
| Только пишете в коллекцию | `? super T` |
| И читаете, и пишете | Без wildcards — просто `T` |
| Тип не важен вообще | `?` |

---

## 7. Type Erasure — стирание типов

Это, пожалуй, самая важная «скрытая механика» дженериков. **Generics существуют только на этапе компиляции.** После компиляции вся информация о параметрах типа стирается — JVM о дженериках ничего не знает.

### Что происходит при стирании

Компилятор заменяет параметры типа:

- `T` без ограничений → `Object`
- `T extends Number` → `Number`
- `T extends Comparable & Serializable` → `Comparable` (первое ограничение)

```java
// Что вы пишете:
public class Box<T> {
    private T content;
    public T get() { return content; }
}

// Что видит JVM после стирания:
public class Box {
    private Object content;
    public Object get() { return content; }
}
```

```java
// Что вы пишете:
public static <T extends Number> double sum(List<T> list) { ... }

// Что видит JVM:
public static double sum(List list) { ... }
// T стёрт до Number
```

### Следствия стирания типов

**1. Нельзя создать экземпляр параметра типа:**

```java
public class Factory<T> {
    public T create() {
        return new T(); // ОШИБКА! JVM не знает, что такое T
    }
}

// Обходной путь — передать Class<T>:
public class Factory<T> {
    private final Class<T> type;

    public Factory(Class<T> type) {
        this.type = type;
    }

    public T create() throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }
}
```

**2. Нельзя создать массив обобщённого типа:**

```java
T[] array = new T[10]; // ОШИБКА!

// Обходной путь:
@SuppressWarnings("unchecked")
T[] array = (T[]) new Object[10];
```

**3. Нельзя использовать `instanceof` с параметром типа:**

```java
if (obj instanceof T) { }        // ОШИБКА!
if (obj instanceof List<String>) // ОШИБКА!

if (obj instanceof List<?>) { }  // OK — unbounded wildcard работает
```

**4. Нельзя перегружать методы, различающиеся только параметрами типа:**

```java
// Не скомпилируется — после стирания оба метода имеют одинаковую сигнатуру
public void process(List<String> strings) { }
public void process(List<Integer> numbers) { }
// Оба превращаются в: process(List list)
```

**5. Статические поля не могут использовать параметр типа класса:**

```java
public class Box<T> {
    private static T defaultValue; // ОШИБКА!
    // Статическое поле одно на весь класс,
    // но Box<String> и Box<Integer> — один и тот же класс после стирания
}
```

---

## 8. Рекурсивные границы типов (Recursive Type Bounds)

Иногда тип должен ссылаться сам на себя в ограничении. Классический пример — `Comparable`:

```java
public interface Comparable<T> {
    int compareTo(T other);
}
```

Когда класс реализует `Comparable`, он сравнивает себя с экземплярами того же класса:

```java
public class Student implements Comparable<Student> {
    private String name;

    @Override
    public int compareTo(Student other) {
        return this.name.compareTo(other.name);
    }
}
```

Метод, работающий с такими типами, использует рекурсивную границу:

```java
// T должен уметь сравниваться с самим собой
public static <T extends Comparable<T>> T max(List<T> list) {
    T result = list.get(0);
    for (T item : list) {
        if (item.compareTo(result) > 0) {
            result = item;
        }
    }
    return result;
}
```

Более продвинутый паттерн — «самотипирование» для цепочек вызовов (fluent API):

```java
public abstract class Builder<T extends Builder<T>> {
    private String name;
    private int age;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T withName(String name) {
        this.name = name;
        return self();
    }

    public T withAge(int age) {
        this.age = age;
        return self();
    }
}

public class StudentBuilder extends Builder<StudentBuilder> {
    private String university;

    public StudentBuilder withUniversity(String university) {
        this.university = university;
        return self();
    }
}

// Цепочка работает без кастинга:
Student s = new StudentBuilder()
    .withName("Alice")    // Возвращает StudentBuilder, не Builder
    .withAge(20)          // Тоже StudentBuilder
    .withUniversity("MIT")
    .build();
```

---

## 9. Wildcard Capture и Helper Methods

Иногда компилятор не может вывести тип wildcard, и нужен вспомогательный метод:

```java
// Эта версия НЕ компилируется:
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i))); // Ошибка! ? не может быть захвачен
}

// Решение — helper method с именованным параметром типа:
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

// Приватный метод "захватывает" wildcard как конкретный T
private static <T> void swapHelper(List<T> list, int i, int j) {
    T temp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, temp);
}
```

Это называется **wildcard capture** — компилятор «захватывает» неизвестный тип `?` и присваивает ему внутреннее имя.

---

## 10. Обобщённые типы и наследование

### Инвариантность обобщённых типов

Напомним: `List<Dog>` **не является** подтипом `List<Animal>`, несмотря на то что `Dog extends Animal`. Это инвариантность.

Но при этом:

```java
// Сам обобщённый тип может быть подтипом:
ArrayList<String> arrayList = new ArrayList<>();
List<String> list = arrayList;       // OK — ArrayList<String> extends List<String>
Collection<String> coll = arrayList; // OK — ArrayList<String> extends Collection<String>
```

То есть подтипирование работает по «внешнему» типу (ArrayList → List), но не по параметру типа (String, Dog, Animal).

### Ковариантность и контравариантность через wildcards

Wildcards позволяют задать нужное поведение:

```java
// Ковариантность — можно подставить подтип:
List<? extends Animal> animals = new ArrayList<Dog>(); // OK

// Контравариантность — можно подставить супертип:
List<? super Dog> dogs = new ArrayList<Animal>();      // OK
```

Для сравнения: массивы в Java ковариантны «из коробки», и это источник ошибок:

```java
Animal[] animals = new Dog[10]; // OK — массивы ковариантны
animals[0] = new Cat();         // Компилируется, но ArrayStoreException в рантайме!
```

Дженерики безопаснее — ошибка ловится на этапе компиляции.

---

## 11. Type Tokens и Super Type Tokens

Из-за стирания типов иногда нужно сохранить информацию о типе вручную:

### Простой Type Token

```java
// Передаём Class<T> как «токен типа»
public <T> T deserialize(String json, Class<T> type) {
    // Используем type для создания объекта нужного класса
}

User user = deserialize(jsonString, User.class);
```

### Проблема с обобщёнными типами

`Class<T>` не может представить обобщённые типы: нет `List<String>.class`.

Решение — **Super Type Token** (паттерн из библиотек вроде Jackson, Gson, Guava):

```java
// Анонимный подкласс сохраняет информацию о типе через рефлексию
TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};

List<String> result = objectMapper.readValue(json, typeRef);
```

Как это работает: при создании анонимного подкласса информация о параметре типа (`List<String>`) сохраняется в байткоде класса, и её можно извлечь через `getGenericSuperclass()`.

---

## 12. Обобщённые типы и лямбды / функциональные интерфейсы

Стандартные функциональные интерфейсы в `java.util.function` активно используют дженерики:

```java
// Function<T, R> — принимает T, возвращает R
Function<String, Integer> length = String::length;

// Predicate<T> — принимает T, возвращает boolean
Predicate<String> isLong = s -> s.length() > 10;

// Consumer<T> — принимает T, ничего не возвращает
Consumer<String> printer = System.out::println;

// Supplier<T> — ничего не принимает, возвращает T
Supplier<List<String>> listFactory = ArrayList::new;
```

Компилятор выводит типы автоматически в цепочках:

```java
List<String> names = List.of("Alice", "Bob", "Carol");

// Каждый шаг цепочки — обобщённый метод, типы выводятся:
List<Integer> lengths = names.stream()       // Stream<String>
    .filter(s -> s.length() > 3)             // Stream<String>
    .map(String::length)                     // Stream<Integer>
    .collect(Collectors.toList());           // List<Integer>
```

### Написание собственного обобщённого функционального интерфейса

```java
@FunctionalInterface
public interface Converter<F, T> {
    T convert(F from);
}

Converter<String, Integer> toInt = Integer::parseInt;
Integer result = toInt.convert("123");
```

---

## 13. Практические паттерны

### Heterogeneous Container (Typesafe Heterogeneous Container)

Паттерн из Effective Java — контейнер, хранящий объекты разных типов типобезопасно:

```java
public class TypeSafeMap {
    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }

    public <T> T get(Class<T> type) {
        return type.cast(map.get(type));
    }
}

TypeSafeMap config = new TypeSafeMap();
config.put(String.class, "hello");
config.put(Integer.class, 42);

String s = config.get(String.class);  // "hello" — без кастинга
Integer n = config.get(Integer.class); // 42 — без кастинга
```

### Generic Factory Method

```java
public static <K, V> Map<K, V> mapOf(K key, V value) {
    Map<K, V> map = new HashMap<>();
    map.put(key, value);
    return map;
}

Map<String, Integer> ages = mapOf("Alice", 25);
```

### Generic Singleton

```java
public class EmptyIterator<T> implements Iterator<T> {
    @SuppressWarnings("rawtypes")
    private static final EmptyIterator INSTANCE = new EmptyIterator();

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> instance() {
        return (Iterator<T>) INSTANCE; // Безопасно — итератор пуст, T не используется
    }

    @Override
    public boolean hasNext() { return false; }

    @Override
    public T next() { throw new NoSuchElementException(); }
}

Iterator<String> emptyStrings = EmptyIterator.instance();
Iterator<Integer> emptyInts = EmptyIterator.instance();
// Оба ссылаются на один и тот же объект — благодаря стиранию типов
```

---

## 14. Частые ошибки и подводные камни

### 1. Raw Types — «сырые» типы

```java
// Плохо — raw type. Теряется вся проверка типов:
List names = new ArrayList();

// Хорошо:
List<String> names = new ArrayList<>();
```

Raw types существуют для обратной совместимости с кодом до Java 5. В новом коде их использовать не нужно. Если тип не важен — используйте `List<?>`, а не `List`.

### 2. Путаница между `List<Object>` и `List<?>`

```java
List<Object> objects = new ArrayList<>();
objects.add("hello"); // OK — можно добавлять
objects.add(42);      // OK

List<?> unknown = new ArrayList<String>();
unknown.add("hello"); // ОШИБКА! Нельзя ничего добавлять
unknown.add(null);     // Единственное исключение
```

`List<Object>` — это конкретный список объектов типа `Object`.
`List<?>` — это список какого-то неизвестного типа.

### 3. Нельзя использовать примитивы

```java
List<int> numbers = new ArrayList<>(); // ОШИБКА!
List<Integer> numbers = new ArrayList<>(); // OK — используем обёртку
```

Java автоматически делает autoboxing/unboxing, но это имеет цену по производительности.

### 4. Ложная безопасность через unchecked cast

```java
@SuppressWarnings("unchecked")
List<String> strings = (List<String>) someObject;
// Этот каст НЕ проверяется в рантайме из-за стирания типов!
// ClassCastException может выстрелить гораздо позже, в неожиданном месте
```

### 5. Heap Pollution — «загрязнение кучи»

Происходит, когда переменная параметризованного типа ссылается на объект другого параметризованного типа:

```java
List<String> strings = new ArrayList<>();
List rawList = strings;          // Raw type — компилятор предупредит
rawList.add(42);                 // Загрязнение кучи
String s = strings.get(0);      // ClassCastException!
```

Особенно опасно при varargs с обобщёнными типами:

```java
@SafeVarargs // Говорим компилятору: мы знаем, что делаем
static <T> List<T> flatten(List<T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```

Аннотация `@SafeVarargs` подавляет предупреждение, но используйте её только если метод действительно не делает ничего опасного с массивом varargs.

---

## 15. Сводная таблица: когда что использовать

| Конструкция | Что означает | Когда использовать |
|---|---|---|
| `<T>` | Именованный параметр типа | Когда тип нужно использовать в нескольких местах |
| `<T extends X>` | Ограниченный параметр | Когда нужны методы типа X |
| `<T extends X & Y>` | Множественные ограничения | Когда нужны методы нескольких типов |
| `?` | Неизвестный тип | Когда тип вообще не важен |
| `? extends T` | Верхняя граница wildcard | Producer — только чтение |
| `? super T` | Нижняя граница wildcard | Consumer — только запись |

---

## 16. Рекомендуемая литература

- **Effective Java** (Joshua Bloch) — главы 26–33, раздел «Generics». Это библия Java-разработчика, и раздел про дженерики — один из лучших
- **Java Generics and Collections** (Maurice Naftalin, Philip Wadler) — единственная книга, посвящённая исключительно дженерикам
- **Официальная документация Oracle** — [The Java Tutorials: Generics](https://docs.oracle.com/javase/tutorial/java/generics/index.html)

---

## Упражнения для практики

1. **Напиши обобщённый стек** (`GenericStack<T>`) с методами `push`, `pop`, `peek`, `isEmpty`. Реализуй на массиве.

2. **Напиши метод `filter`**: `<T> List<T> filter(List<T> list, Predicate<? super T> predicate)` — возвращает новый список из элементов, удовлетворяющих предикату.

3. **Напиши метод `transform`**: `<T, R> List<R> transform(List<? extends T> list, Function<? super T, ? extends R> mapper)` — преобразует каждый элемент.

4. **Реализуй `Pair<A, B>`** с методами `swap()` (возвращает `Pair<B, A>`) и статическим фабричным методом `of(A a, B b)`.

5. **Попробуй сломать типобезопасность** через raw types и unchecked casts. Объясни, где именно и почему происходит `ClassCastException`.

6. **Напиши типобезопасный `EventBus`**: метод `register(Class<T> eventType, Consumer<T> handler)` регистрирует обработчик, метод `post(Object event)` вызывает все подходящие обработчики.
