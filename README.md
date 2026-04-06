# OOP.20252-14
Visualization of operations on tree data structures:
Overview:
 Tree is a fundamental nonlinear data structure widely used in computer science for organizing and processing hierarchical data. In this project, you will design a program that visualizes and explains basic operations on three types of trees — Generic Tree, Binary Tree, and Red-Black Tree.
 The aim is to help users intuitively understand how trees are structured and how operations such as insertion, deletion, traversal, and searching work. This project focuses on applying Object-Oriented Programming (OOP) to model different types of trees and dynamically visualize their behaviors.

Basic knowledge:
Generic Tree:
 A tree structure where each node can have any number of children. It represents general hierarchical data such as file systems or organizational charts.


Binary Tree:
 A specialized tree in which each node has at most two children — typically called the left child and right child. Binary trees form the basis for many efficient data structures like heaps and search trees.


Red-Black Tree:
 A self-balancing binary search tree in which each node has an additional attribute — a color (red or black). The balancing rules ensure that the tree remains approximately balanced, guaranteeing efficient search, insertion, and deletion operations.


Common operations demonstrated:
 Create, Insert, Delete, Update, Traverse, and Search.



Specifications:
GUI:


You can freely design the interface; however, the main objective is to implement the OOP design and visualization logic.


You may refer to this visualization source for inspiration: https://visualgo.net/en/bst.


The GUI should contain:
 • A tree visualization panel (displaying nodes and edges dynamically).
 • A code panel showing algorithm pseudocode or real code, with highlighted execution lines.
 • A bottom control bar for playback (pause, step forward/backward, undo/redo).
Main Menu:
 • Title of the application.
 • Options to select one of three tree types: Generic Tree, Binary Tree, Red-Black Tree.
 • Help menu describing the project’s purpose and basic usage.
 • Quit button (with confirmation dialog).


Visualization Screen:
 • After selecting a tree type, users can perform any of the following operations:


Operation
Parameters
Description
Create
None
Create a new, empty tree.
Insert
Parent node value, new node value
Add a new node under the specified parent (for Generic Tree) or at the correct position (for Binary/Red-Black Tree).
Delete
Node value
Remove the node from the tree (and rebalance for Red-Black Tree if needed).
Update
Current value, new value
Modify the node’s value and refresh visualization.
Traverse
Algorithm (DFS or BFS)
Highlight nodes in the chosen traversal order.
Search
Search value
Highlight the found node or display “not found”.

• During each operation:  
  - The corresponding pseudocode is shown in the code panel, with the currently executing line highlighted.  
  - Users can pause, continue, or step through the execution process.  
  - A progress bar at the bottom indicates how much of the operation has been completed.  
  - Undo/Redo buttons allow users to revisit previous operations.  
• The visualization should clearly show structural changes — new nodes, rebalanced trees, or traversal paths.  
• Always include a Back button to return to the main menu.  
Note:
You must implement tree structures and algorithms manually — do not use built-in data structure libraries.


Optional extensions:


Allow users to adjust animation speed.


Add comparison mode (e.g., visualize Binary Tree vs. Red-Black Tree side by side).


Display statistics such as tree height, number of nodes, or balance factor.
Important note:
For each topic provided below, it is compulsory for you to strictly follow all the specifications stated below. The reference links are there to clarify/illustrate for the specifications only, in case of conflict between the specifications and the reference, the specifications should be prioritized.
Some topics may require a bit more field-specific knowledge than others (topics related to electronics, operating systems,...). In which case, we have provided summarizations and formulae for you to quickly and aptly grasp the knowledge needed. This way, you can focus on the object-oriented aspect of the project, which is the most important goal.

Caution: Since we will only assess the use of object-oriented programming of this project, you should not spend too much time on interface design, which won’t be graded. However, the assignment requires modeling objects in the interface with clearly defined attributes and behaviors. Simulations or animations must be implemented programmatically using logic, rather than relying on pre-made resources such as videos. This approach ensures the proper application of object-oriented programming techniques.
Additionally, you must utilize behavioral polymorphism (e.g., method overriding) to define varied behaviors for subclasses. Avoid limiting polymorphism to object type or static method overloading. Use inheritance, interfaces, and polymorphism to design reusable and extensible code that aligns with the primary goals of the project.
