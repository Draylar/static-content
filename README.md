# Static Content

*Static Content* is a wrapper for [*Static Data*](https://github.com/CottonMC/StaticData) that provides an easy method
 for registering similar content through your mod while also providing cross-mod compatibility & support.
 
 
### Use Cases

This library works best for mods that offer multiple pieces of similar content.
An example would be a library that provides numerous Apple variants (Emerald, Diamond, Iron), because
each Apple only has slight differences (while the core mechanics remain similar). The power of 
*Static Content* in this situation is:

- Allowing you to add Apples through JSON files
- Allowing *other* developers to add optional Apples that will only be registered when your mod is present
 
### Installation

*Static Content* is available through [JitPack](https://jitpack.io/#Draylar/static-content):

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    modImplementation 'com.github.Draylar:static-content:1.0.0'
    include 'com.github.Draylar:static-content:1.0.0'
}
```

### Usage

There are 3 major things you will need to do to port your mod to Static Content:
- Create a class representation of your data
- Populate JSON files with your mods' relevant data
- Tell Static Content to load your files

To start, let us make a simple class for loading Apple types. 
Each Apple will have a name, and a hunger restoration value.

Our class will implement *ContentData*, which is provides a *register* method that is called once per file.
What you do in this method is up to your implementation & use case.

```java
public class AppleData implements ContentData {

    private final String name;
    private final int hunger;

    public AppleData(String name, int hunger) {
        this.name = name;
        this.hunger = hunger;
    }

    @Override
    public void register(Identifier fileID) {
        Registry.register(
                Registry.ITEM,
                new Identifier("applemod", name),
                new Item(new Item.Settings().food(
                        new FoodComponent.Builder()
                                .hunger(hunger)
                                .build()))
        );
    }
}
```

Your data classes' constructor will not be called, but it is still needed to prevent in-lining (which *will* happen without one).
Remember to use your IDE to auto-generate the constructor after assembling your fields.

After creating our initial class, we can tell Static Content to deserialize data files under a certain directory to our class:

```java
@Override
public void onInitialize() {
    StaticContent.load(new Identifier("applemod", "apples"), AppleData.class);
}
```

This will deserialize all JSON files under `resources/static_data/applemod/apples/`. 
Let us make one at `resources/static_data/applemod/apples/emerald_apple.json`:
```json
{
  "name": "emerald_apple",
  "hunger": 6
}
``` 

Start the game...
```
[main/INFO] (Static Content) Loaded 1 Static Content file for applemod:apples
[Worker-Main-12/WARN] (Minecraft) Unable to load model: 'applemod:emerald_apple#inventory' referenced from: applemod:emerald_apple#inventory: java.io.FileNotFoundException: applemod:models/item/emerald_apple.json
```


