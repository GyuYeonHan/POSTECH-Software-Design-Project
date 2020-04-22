# Team1 Refactor

## 1. Requirements 

> Supported Language : Java

> 1) [JDK vesrion : v11.0](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html) 
> 2) [Java 1.8.0](https://www.java.com/en/download/)
> 3) [IntelliJ : v0.4.12+](https://www.jetbrains.com/idea/download/)
> 4) [Jacoco : v0.8.5+](https://www.eclemma.org/jacoco/)
> 5) [Gradle : v4.4.1+](https://gradle.org/install/)
> 
> Only supported for GUI supported OS(OS which can run IntelliJ).

## 2. Installation (No Dependency on OS)

Using git link 

> git clone git@csed332.postech.ac.kr:team1/project.git

Using https link

> git clone https://csed332.postech.ac.kr/team1/project.git

Using clone from Web Page(GUI)
- Press the clone button or use download button. 

![](https://i.imgur.com/VYmlvMa.png)

## 3. Get Started

### Using IntelliJ (MacOSX, Ubuntu, Windows)

> For Windows users, recommended to use this. 
> 

**Building Project**
> Open the Gradle menu and press the button (Tasks > build > build) 
![](https://i.imgur.com/uPSVCVA.png)

**Running Test**
> Open the Gradle menu and press the button (Tasks > verification > test) 
![](https://i.imgur.com/qZ7xPL4.png)

**Running the IDE**
> Open the Gradle menu and press the button (Tasks > intellij > runIde)
![](https://i.imgur.com/ud1nbGy.png)

**Checking the Test Coverage**
> Open the Gradle menu and press the button (Tasks > verification > jacocoTestReport)
> After running this block, enter `build/jacocoHtml`. And open `index.html`
![](https://i.imgur.com/JnTe2TD.png)

### Using Command (Ubuntu, MacOSX)

> All the commands introduced here is in `project` directory. 

**Building Project**
```
gradle build
```

**Running Test**
```
gradle test 
```
**Running the IDE**
```
gradle runide
```
**Checking the Test Coverage**

> After executing the command, enter `build/jacocoHtml`. And open `index.html`. 
```
gradle jacocoTestReport
```

## 4. Program Usage

> You should first write down your code to refactor. 
> Then there are three possible ways to use this program. 

> More details about an usage of each refactoring is introduced in below chapter named `Refactoring Manual`.

### Tab Created in IntelliJ Tab

> There will be a new tab on your IntelliJ. You can select the refactoring among activated blocks. Only refactorable refactorings are activated in the code. 

**Before the refactoring**

![](https://i.imgur.com/UkTd99e.png)

**After the refactoring**

![](https://i.imgur.com/b2bDnjX.png)

### Using Command `Alt` + `Enter`

> When we put the mouse pointer on a refactoring code, all the available refactorings are listed when we press `Alt` + `Enter`.
 
**Before Refactoring**
> Consolidate Duplicate Conditional Fragments activated

![](https://i.imgur.com/Jlpj2DN.png)

**After Refactoring**
> Consolidate Duplicate Conditional Fragments deactivated

![](https://i.imgur.com/QqS05aS.png)

### Using Right Click on Code

> When we use right click over code, we can get `Refactor` tab. Inside the `Refactor` tab, there is `Team1Refactor` tab. When you enter on it, we can get all the refactorable refactorings. 

**Before Refactoring**

![](https://i.imgur.com/3baz6FQ.png)

**After Refactoring**

![](https://i.imgur.com/NYeNnsu.png)

## 5. Refactoring Manual(User Stories)

1. [Collapse Hierarchy](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Collapse-Hierarchy)
2. [Consolidate Duplicate Conditional Fragments](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Consolidate-Duplicate-Conditional-Fragments)
3. [Encapsulate Collection](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Encapsulate-Collection)
4. [Hide Method](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Hide-Method)
5. [Form Template Method](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Form-Template-Method)
6. [Preserve Whole Object](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Preserve-Whole-Object)
7. [Pull Up Constructor Body](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Pull-Up-Constructor-Body)
8. [Replace Subclass with Fields](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Subclass-with-Fields)
9. [Replace Conditional with Polymorphism](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Conditional-with-Polymorphism)
10. [Separate Query From Modifier](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Separate-Query-from-Modifier)
11. [Remove Assignments to Parameter](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Remove-Assignments-to-Parameters)
12. [Replace Method with Method Object](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Method-with-Method-Object)
13. [Split Temporary Variable](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Split-Temporary-Variable)
14. [Replace Exception with Test](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Exception-with-Test)

## 6. Documentation

1. [Github Link](https://csed332.postech.ac.kr/team1/project/tree/master) - Project Link

2. [Wiki - Home](https://csed332.postech.ac.kr/team1/project/wikis/home) - Most of the details of the program are explained here. 

3. [Refactoring Guru](https://refactoring.guru/refactoring/techniques) - Reference about refactoring techniques our projects are based on. 