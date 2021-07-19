## Observer in Swift

> **Observer** is a behavioral design pattern that allows some objects to notify other objects about changes in their state.

The Observer pattern provides a way to subscribe and unsubscribe to and from these events for any object that implements a subscriber interface.

---

### Usage of the pattern in Swift

**Usage examples**: The Observer pattern is pretty common in Swift code, especially in the GUI components. It provides a way to react to events happening in other objects without coupling to their classes.

**Identification**: The pattern can be recognized by subscription methods, that store objects in a list and by calls to the update method issued to objects in that list.


### Conceptual Example

This example illustrates the structure of the **Observer** design pattern. It focuses on answering these questions:


- What classes does it consist of?
- What roles do these classes play?
- In what way the elements of the pattern are related?

After learning about the pattern’s structure it’ll be easier for you to grasp the following example, based on a real-world Swift use case.

#### Example1.swift: Conceptual example

```
import XCTest

/// The Subject owns some important state and notifies observers when the state
/// changes.
class Subject {

    /// For the sake of simplicity, the Subject's state, essential to all
    /// subscribers, is stored in this variable.
    var state: Int = { return Int(arc4random_uniform(10)) }()

    /// @var array List of subscribers. In real life, the list of subscribers
    /// can be stored more comprehensively (categorized by event type, etc.).
    private lazy var observers = [Observer]()

    /// The subscription management methods.
    func attach(_ observer: Observer) {
        print("Subject: Attached an observer.\n")
        observers.append(observer)
    }

    func detach(_ observer: Observer) {
        if let idx = observers.firstIndex(where: { $0 === observer }) {
            observers.remove(at: idx)
            print("Subject: Detached an observer.\n")
        }
    }

    /// Trigger an update in each subscriber.
    func notify() {
        print("Subject: Notifying observers...\n")
        observers.forEach({ $0.update(subject: self)})
    }

    /// Usually, the subscription logic is only a fraction of what a Subject can
    /// really do. Subjects commonly hold some important business logic, that
    /// triggers a notification method whenever something important is about to
    /// happen (or after it).
    func someBusinessLogic() {
        print("\nSubject: I'm doing something important.\n")
        state = Int(arc4random_uniform(10))
        print("Subject: My state has just changed to: \(state)\n")
        notify()
    }
}

/// The Observer protocol declares the update method, used by subjects.
protocol Observer: class {

    func update(subject: Subject)
}

/// Concrete Observers react to the updates issued by the Subject they had been
/// attached to.
class ConcreteObserverA: Observer {

    func update(subject: Subject) {

        if subject.state < 3 {
            print("ConcreteObserverA: Reacted to the event.\n")
        }
    }
}

class ConcreteObserverB: Observer {

    func update(subject: Subject) {

        if subject.state >= 3 {
            print("ConcreteObserverB: Reacted to the event.\n")
        }
    }
}

/// Let's see how it all works together.
class ObserverConceptual: XCTestCase {

    func testObserverConceptual() {

        let subject = Subject()

        let observer1 = ConcreteObserverA()
        let observer2 = ConcreteObserverB()

        subject.attach(observer1)
        subject.attach(observer2)

        subject.someBusinessLogic()
        subject.someBusinessLogic()
        subject.detach(observer2)
        subject.someBusinessLogic()
    }
}
```


#### OutputConceptual.txt: Execution result

```
Subject: Attached an observer.

Subject: Attached an observer.


Subject: I'm doing something important.

Subject: My state has just changed to: 4

Subject: Notifying observers...

ConcreteObserverB: Reacted to the event.


Subject: I'm doing something important.

Subject: My state has just changed to: 2

Subject: Notifying observers...

ConcreteObserverA: Reacted to the event.

Subject: Detached an observer.


Subject: I'm doing something important.

Subject: My state has just changed to: 8

Subject: Notifying observers...
```

---

### Real World Example


#### Example2.swift: Real world example

```
import XCTest

class ObserverRealWorld: XCTestCase {

    func test() {

        let cartManager = CartManager()

        let navigationBar = UINavigationBar()
        let cartVC = CartViewController()

        cartManager.add(subscriber: navigationBar)
        cartManager.add(subscriber: cartVC)

        let apple = Food(id: 111, name: "Apple", price: 10, calories: 20)
        cartManager.add(product: apple)

        let tShirt = Clothes(id: 222, name: "T-shirt", price: 200, size: "L")
        cartManager.add(product: tShirt)

        cartManager.remove(product: apple)
    }
}

protocol CartSubscriber: CustomStringConvertible {

    func accept(changed cart: [Product])
}

protocol Product {

    var id: Int { get }
    var name: String { get }
    var price: Double { get }

    func isEqual(to product: Product) -> Bool
}

extension Product {

    func isEqual(to product: Product) -> Bool {
        return id == product.id
    }
}

struct Food: Product {

    var id: Int
    var name: String
    var price: Double

    /// Food-specific properties
    var calories: Int
}

struct Clothes: Product {

    var id: Int
    var name: String
    var price: Double

    /// Clothes-specific properties
    var size: String
}

class CartManager {

    private lazy var cart = [Product]()
    private lazy var subscribers = [CartSubscriber]()

    func add(subscriber: CartSubscriber) {
        print("CartManager: I'am adding a new subscriber: \(subscriber.description)")
        subscribers.append(subscriber)
    }

    func add(product: Product) {
        print("\nCartManager: I'am adding a new product: \(product.name)")
        cart.append(product)
        notifySubscribers()
    }

    func remove(subscriber filter: (CartSubscriber) -> (Bool)) {
        guard let index = subscribers.firstIndex(where: filter) else { return }
        subscribers.remove(at: index)
    }

    func remove(product: Product) {
        guard let index = cart.firstIndex(where: { $0.isEqual(to: product) }) else { return }
        print("\nCartManager: Product '\(product.name)' is removed from a cart")
        cart.remove(at: index)
        notifySubscribers()
    }

    private func notifySubscribers() {
        subscribers.forEach({ $0.accept(changed: cart) })
    }
}

extension UINavigationBar: CartSubscriber {

    func accept(changed cart: [Product]) {
        print("UINavigationBar: Updating an appearance of navigation items")
    }

    open override var description: String { return "UINavigationBar" }
}

class CartViewController: UIViewController, CartSubscriber {

    func accept(changed cart: [Product]) {
        print("CartViewController: Updating an appearance of a list view with products")
    }

    open override var description: String { return "CartViewController" }
}
```


#### OutputRealWorld.txt: Execution result

```
CartManager: I'am adding a new subscriber: UINavigationBar
CartManager: I'am adding a new subscriber: CartViewController

CartManager: I'am adding a new product: Apple
UINavigationBar: Updating an appearance of navigation items
CartViewController: Updating an appearance of a list view with products

CartManager: I'am adding a new product: T-shirt
UINavigationBar: Updating an appearance of navigation items
CartViewController: Updating an appearance of a list view with products

CartManager: Product 'Apple' is removed from a cart
UINavigationBar: Updating an appearance of navigation items
CartViewController: Updating an appearance of a list view with products
```
