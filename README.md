# rwep
randomized weighted elements provider
## Usage

```java
...
        // setup source
        Map<String, Double> source = new HashMap<>();
        source.put("key", 100D);
        source.put("yek", 1D);
        
        // create producer
        Producer<String> producer = new Producer<>(source);
        
        // pick one random element
        String key = producer.produce();
        assert key.equals("key");
        
        // pick two random elements
        Collection<String> collection = producer.produce(2);
        assert collection.contains("key") && collection.size() == 2 && !collection.contains("yek");
        
        // pick two random unique elements
        Collection<String> unicCollection = producer.produce(2, true);
        assert unicCollection.contains("key") && unicCollection.contains("yek");
        
        
        // stateless style
        assert "key".equals(Producer.produce(source));
...
