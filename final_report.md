# Team 1 Final Report


### Team Member

[ByoungYun Park](@bakbang), [Han Gyu Yeon](@hangy1020), [JaeHueng Choi](@pish11010), [Jiwan Kang](@jiwankang98), [Kang Minsu](@powerful), [KimDonghyun](@gimdh), [LeeJungEun](@jelee), [MoonYoungatae](@moonyoungtae), [YoonWooJeong](@jeongyw12382)

## A. Brief Description of Project

이 프로젝트는 `Intelij IDEA` 에서 `Java Language`의 `Automated Code Refactoring`을 수행하는 플러그인이다. 소프트웨어 개발 과정에서 리팩토링은 좀 더 이해하기 쉽고 추후에 코드를 잘 보수하기 위해서도 필수적으로 진행되어야 하는데 이러한 리팩토링 과정을 좀 더 손쉽게 하고자 이러한 플러그인을 구현했다.  

우리가 구현한 `Refactoring Technique`들은 https://refactoring.guru/refactoring/techniques 에 있는 기능들을 참고하였다. 그러나 기본적으로 `Intelij IDEA`에서도 리팩토링을 지원하기 때문에, 좀 더 의미있는 플러그인을 구현하기 위해 그 중 `Intelij IDEA`에서 지원하지 않는 `Refactoring Technique`을 선별하여 구현하였다. 이러한 작업을 위해 처음 리팩토링 기능과 관련된 26개의 `User Stroy`를 작성했고 그 중에서 15개, 추후에 추가로 2개(GUI와 Improved Test) 총 17개의 `User Stroy`를 구현했다. `User Story`의 상세 설명은 `Project Wiki`에서 볼 수 있다. 

## B. Practices in XP of This Project

### Planning Game

Iteration 1에서 프로젝트에서 구현할 User Story들을 정하고 각각에 대한 story point를 정하였다. 실제 Planning Poker 카드를 이용하면 좋겠지만 카드가 없었기 때문에 Team Leader의 하나, 둘, 셋 하는 신호에 따라 모든 팀원이 동시에 손가락을 이용해 자신이 생각하는 story point를 표현하였다. 그 후, 제일 작은 story point를 제시한 사람과 제일 큰 story point를 제시한 사람이 각각 자신의 예측에 대한 이유를 밝히고, 서로의 의견을 조율하였다. 

이 과정을 story point에 대한 모두의 의견의 합의가 이루어질때까지 반복하였다. 다만, 모든 이가 같은 story point를 제시할 때까지 Planning Game을 계속하는 것은 비효율적이라고 생각하여, 80% 이상의 인원이 같은 story point를 제시하고 이외 인원도 그 story point에 동의할 경우 story point를 확정하였다.

그 후, Iteration 2에서 완수할 수 있을 만한 story point를 예측하여 User Story를 할당하였고, Iteration 3를 시작하면서는 이미 진행했던 Iteration 2에서의 경험을 토대로 User Story에 대한 story point를 재조정하는 과정을 거지고 Iteraion2의 velocity를 고려하여 User Story를 할당하였다.

### Pair Programming

매번 iteration마다 5개의 pair를 새로 정하여 두 명이서 Pair Programming을 진행하였다. 단, 총 인원이 9명으로 홀수이기 때문에 마지막 pair는 혼자서 작업을 진행하였다. commit message 마지막에 있는 pair의 언급으로(ex. @jelee) Pair Programming을 확인할 수 있다.

### Test Driven Development

실제 기능을 구현하기 전에 미리 해당 기능의 Black Box Test를 작성하였다. 이러한 테스트들은 모두 Automated Test로 작성했다. 이 Test들은 처음엔 모두 실패하며 이 실패한 Test를 충족하기 위한 기능 코드를 작성하는 방식으로 진행했다. 팀원 대부분이 TDD에 익숙하지 않기 때문에, 최대한 commit도 test 구현과 코드 구현을 나누어 하도록 하였다. commit log를 통해 이를 확인할 수 있다.

### Refactoring

User Story 구현 시, 각각의 페어별로 코드를 작성 후 간단하게 불필요한 코드를 제거하는 작업을 진행했다. 또한 TDD로 작업을 진행하였기 때문에, 각 refactoring 후 test를 실행해보면서 코드의 기능이 변화하지 않았는지를 확인할 수 있었다.

프로젝트를 완성하고 보니 Comment나 전체적인 구조 등 Refactoring해야 할 부분이 많이 보였다. 그래서 Iteration 3 마지막에 Refactoring Branch를 만들어 전체 코드에 대해 간단한 설명을 위한 주석을 달고 가독성이 떨어지는 부분을 리팩토링 했다. 또 전체적인 Coding Convention과 Code Format을 통일하는 작업을 진행했다. 

### Continuous Integration

프로젝트 초반에 gitlab의 project에 CI를 구축하는 작업을 수행하였다. gitlab의 자체적인 CI가 있지만, coverage report를 확인하기에 불편하다는 의견이 있어 Jenkins도 추가하였다. commit을 push할 때마다 이 CI를 이용해 모든 test를 통과하는지, coverage는 어떻게 되는지를 바로바로 확인할 수 있었다. 

## C. Architecture and design

### 1. Action

#### CommonAction

모든 기본적인 리팩토링 액션에 대한 Superclass. 기본적으로 Action이 수행되면, `actionPerformed`에서 `isRefactorable`을 실행하여 현재 코드가 refactoring 가능한지를 확인하여, `true`를 반환하는 경우에만 `runRefactoring`을 수행한다.

<img src="https://i.imgur.com/Ovo9RKd.png" style="zoom:70%" />



**CommonAction을 상속하는 subClass들이 구현해야 하는 Method**

abstract 메소드로서, 반드시 구현해야 함.

- `isRefactorable`: 현재 코드가 해당 Refactoring의 실행 조건에 맞는지를 확인하여, refactoring 가능하면 true, 불가능하면 false를 반환한다.
- `runRefactoring`: 실제 refactoring 액션을 수행한다. 

**Intention Action, 우클릭을 이용해 리팩토링하기 위한 주요 Methods**

- `update()`: action이 refactorable한지를 `isRefactorable`을 통해 확인하여 해당 액션의 enable / disable 상태를 변경해준다. 
- `invoke()`: 사용자가 intention Action을 호출할 경우 runRefactoring을 수행함.
- `startInWriteAction()`: write action 시에는 intention action을 보여주지 않을 것이므로, false를 반환함.
- `isAvailable()`: `isRefactorable`을 통해 해당 액션이 refactorable한지를 확인하여 intention action에 보여줄지 말지 여부를 결정한다.

- `getText()`, `getRefactorName()`, `getFamilyName()`: GUI 상의 text 표시를 위해 필요한 메서드


#### 실제 Refactoring Action

User Story에 맞는 리팩토링 액션을 구현하기 위해서는 `CommonAction`을 상속하는 클래스를 만들어, `isRefactorable()`과 `runRefactoring()` 메서드를 구현하여야 한다. 우리가 iteration2, 3동안 실제로 구현한 Action은 다음과 같다. 액션에 대한 자세한 설명은 각각의 이름에 링크되어 있는 User Story wiki page에서 확인할 수 있다.

- [`CollapseHierarchyAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Collapse-Hierarchy)
- [`ConsolidateDuplicateConditionalFragmentsAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Consolidate-Duplicate-Conditional-Fragments)
- [`EncapsulateCollectionAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Encapsulate-Collection)
- [`FormTemplateMethodAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Form-Template-Method)
- [`HideMethodAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Hide-Method)
- [`PreserveWholeObjectAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Preserve-Whole-Object)
- [`PullUpConstructorBodyAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Pull-Up-Constructor-Body)
- [`RemoveAssignmentsToParametersAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Remove-Assignments-to-Parameters)
- [`ReplaceConditionalWithPolymorphismAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Conditional-with-Polymorphism)
- [`ReplaceExceptionWithTestAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Exception-with-Test)
- [`ReplaceMethodWithMethodObjectAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Method-with-Method-Object)
- [`ReplaceSubclassWithFieldsAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Replace-Subclass-with-Fields)
- [`SeparateQueryFromModifierAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Separate-Query-from-Modifier)
- [`SplitTemporaryVariableAction`](https://csed332.postech.ac.kr/team1/project/wikis/%5BUser-Story%5D-Split-Temporary-Variable)

### 2. Utils

Action과 직접적으로 관련없거나 Action 구현에 도움이 되는, 모두가 공유하여 쓸 수 있는 메서드들은 Utils를 만들어 구현하였다. 용도에 따라 TextUtils, EditorUtils, TreeUtils 등으로 구분하였다.

#### [TextUtils](https://csed332.postech.ac.kr/team1/project/blob/master/src/main/java/team1/plugin/utils/TextUtils.java)

Refactoring의 대상이 되는 file내의 text의 분석을 위해 사용되는 Util. `method`, `conditional branch(if, switch, ...)`, `local variable`등을 찾아주는 동작을 지원함. 이 외에도 `findIdentifiers()`, `findReturnStatements()`와 같은 다양한 메소드들이 구현되어 있음.

#### [EditorUtils](https://csed332.postech.ac.kr/team1/project/blob/master/src/main/java/team1/plugin/utils/EditorUtils.java)

Refactoring의 동작을 위해 필요한 여러 기능에 대한 지원을 위해 사용되는 Util.  `replacePsiElement`, `autoIndentation`등의 동작을 지원함.이 외 `getFocusedClass()`, `getPsiDirectory()`와 같은 다양한 메소드들이 존재함.

#### [TreeUtils](https://csed332.postech.ac.kr/team1/project/blob/master/src/main/java/team1/plugin/utils/TreeUtils.java)

Refactoring 대상 코드의 Refactoring수행 전과 후에 대한 비교를 위해 사용되는 Util. `codeEqual()`과 `treeEqual()`을 통해 코드에 대한 구조적인 분석을 지원함.

#### [DialogUtils](https://csed332.postech.ac.kr/team1/project/blob/master/src/main/java/team1/plugin/utils/DialogUtils.java)

Refactoring 과정 중 필요한 여러 안내 문구를 사용하기 위한 Util. `info`, `error`, `input`등의 동작을 지원함.

#### [MethodCollection](https://csed332.postech.ac.kr/team1/project/blob/master/src/main/java/team1/plugin/utils/MethodCollection.java)

몇몇 리팩토링은 상속받은 클래스에 대해서 진행되는 경우가 있다. 한 클래스가 있고 그 클래스를 상속하는 하위 클래스가 있을 때, 하위 클래스의 메소드 들에 대한 목록을 구성하기 위해 구현한 구조이다. 세 가지의 멤버 변수가 존재하는데 parent는 상위 클래스를 저장하고 있고, methodName는 하위 클래스의 메소드 이름을 저장하고 있고, methods는 같은 클래스를 부모로 갖는 서로 다른 하위 클래스에 동일 명의 메소드가 존재할 경우 이 메소드를 가리키는 PsiMethod 목록을 저장한다.



### 3. 테스트

Iteration2까지 모든 리팩토링 액션은 단순한 string 비교를 통해서 리팩토링 전후의 코드를 비교하였다. 이 방식을 개선하여, Iteration3에서는 PSI Tree 모델을 만들어 리팩토링 전후의 코드의 동등성을 비교하는 기능을 추가적으로 구현하였다. 테스트함수에서는 `TextUtils` 혹은 `TreeUtils`안의 `codeEqual` 함수를 이용하여 두 코드를 비교할 수 있다.

**1) String 비교**

`TextUtils` 내의 `codeEqual()` 함수를 통해 두 코드의 String 자체를 비교할 수 있다. 이 함수에서는, 두 코드의 모든 whitespace를 제거한 두 string을 비교하여 true 혹은 false를 반환해준다.

**2) PSI Tree 비교**

#### FileStrucureTreeFactory

두 코드의 PSI Tree 비교를 위해, 파일을 받아서 그 안의 코드를 이용하여 PSI Tree Model을 만들어준다. 

- `createFileTreeModel()`: Tree model을 만드는 기본 함수이다. JavaElementVisitor를 이용해 File 내의 모든 PsiElement를 visit하여 Leaf / NonLeaf Node인지에 따라 TreeModel을 만든다. whitespace는 무시한다.

- `leafAdd()`: TreeModel의 leaf node에 해당 PsiElement를 추가한다.


#### TreeUtils

- `codeEqual()`: 비교할 두 코드를 받아서 새로운 PsiFile을 만들고, 각각의 파일에 대해FileStructureTreeFactory의 `createFileTreeModel()`을 이용하여 PsiTree를 만든 후 `treeEqual()`을 이용해 PsiTree를 비교한다.
- `treeEqual()`: DFS(Depth First Search)를 이용해서 두 트리가 똑같은지를 검사한다.




## D. Discussion

#### 1) 완벽하지 못한 Refactoring Action

기본적으로 https://refactoring.guru/refactoring/techniques 에 있는 예시를 기준으로 작성하였다. 하지만 구현했던 리팩토링 기법들 중 몇몇은 너무 추상적인 상황을 리팩토링 해야하는 경우가 있었다. 그러다 보니 Refactoring Technique의 전반적인 구조를 통해 리팩토링하기 보다 구체적인 `Code Pattern`을 찾아 리팩토링을 수행한 경우가 많은데 해당 리팩토링 기법의 전체적인 구조를 반영하지 못했다.  

이러한 예로 `Conditional`에 대해 수행하는 리팩토링이라면 `if`, `while`, `for`, `switch` 등 여러 가지가 있는데 `if`에 대해서만 리팩토링이 진행된 점과, `Exception`을 변환하는 리팩토링이나 `Collection`을 변환하는 리팩토링은 정해진 종류의 `Exception`이나 `Collection`에 대해서만 리팩토링이 수행된 점을 들 수 있다. 그래서 구현된 기능들이 리팩토링 기법의 이름에서 생각할 수 있는 것보다 제약적인 경우가 많은데 이러한 것에 대해서는 각각의 User story Wiki에 상세하게 적어 놓았다.

#### 2) Efficiency of Program 

보통 우리가 진행한 리팩토링이 한 파일 내에서만 진행되는 경우가 많다. 그러다 보니 엄청나게 많은 양의 코드에 대해 리팩토링이 진행될 경우에 대한 고려를 하지 않았고, 또 매 주 회의에서 기능 구현을 우선으로 얘기하다 보니 플러그인 동작의 효율성에 대한 고려를 많이 하지 않았다. 실제로 현재 코드에서 리팩토링 탭을 누르거나 `Alt` + `Enter` 를 눌러 `Intention Action`을 실행하는 경우 현재 실행되고 있는 파일의 코드에서 리팩토링이 가능한지 우리가 구현한 기능들에 대해 모두 한번 씩 검사를 하게 되는데 사실 불필요한 작업이 반복될 수 있으므로 비효율적이다.  

이러한 구조는 현재 프로젝트 기능의 규모가 작기 때문에 우리가 개발하던 규모에서는 큰 문제로 느껴지지 않을 수도 있지만 규모가 커지게 된다면 전체 플러그인의 동작이 굉장히 느려질 수 있을 것이다. 이러한 문제의 해결책으로 다른 파일의 리팩토링 가능 여부는 캐싱해두고 실시간으로 바뀐 코드에 대해서만 리팩토링 가능 여부를 다시 판단한다면 프로그램 효율성을 증가시킬 수 있을 것이다.

#### 3) Code Integration Problem

기능을 구현할 때 각각의 페어별로 작업하면서 협업을 위해 `Git`을 이용했는데, 사실 `Git`에 익숙한 조원이 많이 없다보니 처음에는 Git Branch History가 굉장히 복잡해지기도 하고, 각각 페어에서 작업한 기능들을 Merge할 때, 보통 Merge Conflict에 대해서만 주의 했는데 사실 실제 Merge 후, 각각의 기능들이 충돌하는 경우가 있었다.  

예를 들어, 처음에는 Tab에서 실제 리팩토링 기능을 눌러야 리팩토링이 가능한지를 알 수 있었다. 이후 개선된 GUI를 구현하면서 직접 각각의 버튼을 누르지 않더라도 리팩토링이 가능한 지 여부를 체크하게 구조가 바뀌었는데, 처음 구현할 때 이러한 구조를 생각하고 구현하지 않았기 때문에 개선된 GUI를 Merge하면서 일부 충돌이 생겼다. 이런 문제를 예방하기 위해 자신의 로컬에서뿐만 아니라 전체적인 예외를 처리할 수 있는 코드를 작성하는 것이 중요하다고 생각되었다.

#### 4) XP에서의 Practices

이번 프로젝트를 `XP` 방식으로 진행하면서 직접 `User story`를 진행해보고 세 번의 `Iteration`을 진행하면서 `Planning Game`, `Test Driven Development`, `Pair Programming` 등을 경험할 수 있었다.  

`Planning Game` 같은 경우는 처음 작성한 `User story`를 각각의 페어에게 균등하게 분배하고 실제 작업을 수행하면서 우리 팀의 `Velocity`를 측정하였고, `Iteration 2`에서의 피드백을 하며 처음 시작할 때 우리의 무지로 잘못 평가된 `Stroy point`를 적절히 수정하면서 잘 이루어 진 것 같다.  

그러나 `TDD` 같은 경우는 테스트를 먼저 작성하고 해당 테스트를 만족하는 코드를 작성하는 방식은 으로 잘 진행되었지만 첫번째 Discussion에서도 얘기했듯이 전반적인 구조를 반영하는 테스트를 작성하지 못한 것 같다.  

마지막으로 `Pair Programming` 같은 경우 2인 1조로 작업을 하면서 서로 실수하거나 아이디어를 생각할 때 큰 도움이 되었다. 하지만 우리 프로젝트에서 각 페어별로 분배받은 기능을 구현하는 것을 목표로 하였고 서로 작성한 기능을 회의 때 구현하는데 그쳤기 때문에 서로의 코드 구현을 이해하는데, 특히 GUI 구현을 이해하는 데 미흡했던 것 같다.

#### 5) Coverage

이번 프로젝트에서 `Automated Test`를 적용하였고 최종적으로 `Branch Coverage`가 78% 가량으로 측정되었다. 우리가 구현할 때 최소 80%를 넘기기로 합의했었는데, 사실 많은 코드들이 GUI 코드를 가지고 있고 이는 `Automated Test`로 구현하지 못했는데 이러한 GUI 부분을 제외한다면 우리 생각한 `Coverage`를 충분히 넘길 것으로 생각한다.



## Appendix

### Team1Refactor 실행 방법

[README.md](https://csed332.postech.ac.kr/kmbae/csed332-project/blob/master/README.md) 참고

### Team1Refactor 새로운 Refactor 기능 추가 방법

아래와 같은 경로를 따라, 추가하고자 하는 Refactor 기능의 이름에 맞게 java file을 추가(예시는 `HelloAction`)

<img src="https://i.imgur.com/8BFA1FL.png" style="zoom:60%"/>

Action에 해당하는 java file은 `CommonAction`을 상속받도록 한 뒤, 아래와 같이 `isRefactorable`과 `runRefactoring`은 필수적으로 구현해야 사용가능

<img src="https://i.imgur.com/nWPz5ox.png" style="zoom:70%"/>

추가한 Action이 GUI에서 사용될 수 있도록 아래와 같은 경로의 `plugin.xml` 수정필요

<img src="https://i.imgur.com/ziQQOev.png" style="zoom:60%"/>

수정은 아래와같이 `<extensions>`와 `<actions>`에 적절하게 추가

<img src="https://i.imgur.com/nCuWSgv.png" style="zoom:70%"/>

<img src="https://i.imgur.com/acfc495.png" style="zoom:70%"/>

`runRefactoring`에서 의도된 대로, HelloAction이 활성화 되어있으며 

<img src="https://i.imgur.com/izK4xzy.png" style="zoom:50%"/>

클릭 시 `HelloAction runned!!!`가 띄워짐을 확인 가능

<img src="https://i.imgur.com/r6QQeHU.png" style="zoom:50%"/>



### Team1Refactor test 방법

#### Automated Test

아래와 같은 경로를 따라, 테스트하고자 하는 Action의 이름에 맞게 java file을 추가(예시는 `HelloActionTest`)

<img src="https://i.imgur.com/7rMGZfx.png" style="zoom:50%"/>

Test 해당하는 java file은 `LightPlatformTestCase`를 상속받도록 한 뒤, 아래와 같이 `test`를 prefix로 하는 이름을 가진 method를 만들어 사용가능

<img src="https://i.imgur.com/oqFehI6.png" style="zoom:60%"/>
