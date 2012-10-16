# DSBW Lab repository

This is the repository that you need to fork for the DSBW Labs. Every group will have a fork of this repository and will work on it's own copy.

For the first lab, you have to replace this text for whatever text you want as long as it includes a link to the running version of the application.

## Design concepts

### Services Layer

 * ChirpsApi: The class cointaing the whole API for the system. It depends on repositories to interact with the Data Access Layer.
 * Chirp, Author, etc: API entities. Classes documenting the structure of common JSON objects returned by the API to the client.

### Data Access Layer

 * Repository: The facade to the Data Access Layer for the Services Layer. It only exposes those methods needed. It usually returns records and other data structures.
 * Record: A case class that represents the structure of documents stored in a collection. e.g. A ChirperRecord defines the structure we expect documents in the chirper MongoDB collection to have.
 * DAO: Data Access Object. A class used to query the MongoDB database. It extends MongoDao therefore exposing that rich api.
 * DB: A class representing the whole MongoDB database. It has values for every collection we'll use, and methods to drop, reset and initialize the DB.
 * mongo._: Package for the generic MongoDao base class which DAOs extend

### Glue Code

  * Dependency Injection is used by hand.
    * There are no singleton objects. Just create one instance of a class with all the dependencies injected in the constructor.
  * Config: An object representing all the configuration options of the system. Values are read from the execution environment, but they have sensible defaults for developers.

### Other classes (use them as libraries)

  * mongo.SalatContext: A configuration trick for Salat (the MongoDB driver used) to use type hints as desired.
  * json: Package for the JSON class, a marker class for Strings representing a JSON document.
    * It includes implicit conversions toJSON(a:Any):JSON and fromJSON\[T\]\(j:JSON\):T
  * server: Package for the API server. It takes an API and serves it in a Servlet.