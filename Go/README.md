## Observer in Go

> **Observer** is a behavioral design pattern that allows some objects to notify other objects about changes in their state.

The Observer pattern provides a way to subscribe and unsubscribe to and from these events for any object that implements a subscriber interface.

---

### Conceptual Example

In the e-commerce website, items go out of stock from time to time. There can be customers who are interested in a particular item that went out of stock. There are three solutions to this problem:

 1. The customer keeps checking the availability of the item at some frequency.
 2. E-commerce bombards customers with all new items available, which are in stock.
 3. The customer subscribes only to the particular item he is interested in and gets notified if the item is available. Also, multiple customers can subscribe to the same product.

Option 3 is most viable, and this is what the Observer pattern is all about. The major components of the observer pattern are:

- Subject, the instance which publishes an event when anything happens.
- Observer, which subscribes to the subject events and gets notified when they happen.
 

#### subject.go: Subject

```
package main

type subject interface {
    register(Observer observer)
    deregister(Observer observer)
    notifyAll()
}
```


#### item.go: Concrete subject

```
package main

import "fmt"

type item struct {
    observerList []observer
    name         string
    inStock      bool
}

func newItem(name string) *item {
    return &item{
        name: name,
    }
}
func (i *item) updateAvailability() {
    fmt.Printf("Item %s is now in stock\n", i.name)
    i.inStock = true
    i.notifyAll()
}
func (i *item) register(o observer) {
    i.observerList = append(i.observerList, o)
}

func (i *item) deregister(o observer) {
    i.observerList = removeFromslice(i.observerList, o)
}

func (i *item) notifyAll() {
    for _, observer := range i.observerList {
        observer.update(i.name)
    }
}

func removeFromslice(observerList []observer, observerToRemove observer) []observer {
    observerListLength := len(observerList)
    for i, observer := range observerList {
        if observerToRemove.getID() == observer.getID() {
            observerList[observerListLength-1], observerList[i] = observerList[i], observerList[observerListLength-1]
            return observerList[:observerListLength-1]
        }
    }
    return observerList
}
```


#### observer.go: Observer

```
package main

type observer interface {
    update(string)
    getID() string
}
```


#### customer.go: Concrete observer

```
package main

import "fmt"

type customer struct {
    id string
}

func (c *customer) update(itemName string) {
    fmt.Printf("Sending email to customer %s for item %s\n", c.id, itemName)
}

func (c *customer) getID() string {
    return c.id
}
```


#### main.go: Client code

```
package main

func main() {

    shirtItem := newItem("Nike Shirt")

    observerFirst := &customer{id: "abc@gmail.com"}
    observerSecond := &customer{id: "xyz@gmail.com"}

    shirtItem.register(observerFirst)
    shirtItem.register(observerSecond)

    shirtItem.updateAvailability()
}
```


#### output.txt: Execution result

```
Item Nike Shirt is now in stock
Sending email to customer abc@gmail.com for item Nike Shirt
Sending email to customer xyz@gmail.com for item Nike Shirt
```

