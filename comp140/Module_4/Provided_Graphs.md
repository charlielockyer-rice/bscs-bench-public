#### **Loading the Test Graphs**

For this module, we are giving you a function`load_test_graph(name)`to help you run your own tests and also to replicate the tests run by OwlTest, should you find your code failing them. This function is defined within`comp140_module4`, which you will import as`movies`, and takes as its input a String representing the name of the graph.**Valid names are "line", "clique", "grid", "tree", "asymmetric1", and "asymmetric2"**. Given one of these names,`load_test_graph`will return the specified graph as an`Graph`object.

So, for instance, to get the line graph, you could do the following:
```
import comp140_module4 as movies
graph = movies.load_test_graph("line")
```

Now you can use the`Graph`object, which has been stored in the variable`graph`.

#### **Movie Graph Structure**

We are also providing you with graphs encoding data from IMDb ([http://www.imdb.com/](http://www.imdb.com/)). These are undirected graphs, where a node represents an actor or actress and an edges are drawn between nodes that have starred in one or more movies together. Each edges is annotated with the set of common movies. Furthermore, in order to use these graphs you need to understand how to access their various properties (nodes, edges, etc.). All of the graphs that we are providing you with implement[this interface](https://canvas.rice.edu/courses/33582/pages/graph-class-api).

#### **Loading the Movie Graphs**

We are giving you two functions,`load_graph(name)`and`load_actors(name)`to help you load the provided movie graphs into memory. As with`load_test_graph`, these functions are defined within the`comp140_module4`module. Both take a single input, a String representing the name of the graph.**Valid names are "subgraph12", "subgraph50", "subgraph100", "subgraph500", "subgraph1000", and "subgraph5000"**. Each of these represents a subgraph of the IMDb graph, where the number is the number of nodes in the graph. We highly recommend that you begin your testing by using the smaller graphs, or better yet, the test graphs (see above). Likeload_test_graph,`load_graph`will return the specified graph as an`Graph`object.`load_actors`will return a list of the nodes in the specified graph. Note that`load_actors`will not give you all of the nodes in the graph (though the[Graph API](https://canvas.rice.edu/courses/33582/pages/graph-class-api)provides this functionality, if you are interested); instead, it will give you a subset of (hopefully recognizable) actors that you may find useful for testing purposes.

#### **Images of the Test Graphs**

The following images depict the structure of some of the test graphs:

line:

clique:

grid:

tree: