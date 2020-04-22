package team1.plugin.utility;

import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;

public class TreeUtilBasicTests extends LightPlatformTestCase {

    public void testBlank(){

        String s1 = "";
        String s2 = "";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));
    }

    public void testEqualCode(){

        String s1 = "class Test{\n" +
                "\n" +
                "    public void testTreeUtils(String a, String b){\n" +
                "        if(a.equals(\"\")){\n" +
                "            return;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(1){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                    return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "class Test{\n" +
                "\n" +
                "    public void testTreeUtils(String a, String b){\n" +
                "        if(a.equals(\"\")){\n" +
                "            return;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(1){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                    return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));

    }

    public void testSpaceDifference(){

        String s1 = "class Test{\n" +
                "\n" +
                "    public void testTreeUtils(String a, String b){\n" +
                "        if(a.equals(\"\")){\n" +
                "            return;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(1){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                    return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        String s2 = "class Test{\n" +
                "\n" +
                "    public void testTreeUtils  (String a, String b){\n" +
                "        if(a.equals(\"\")  ){\n" +
                "            return   ;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(  1  ){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                          return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "\n" +
                "\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));
    }

    public void testEqualCodeInstanceAdded(){

        String s1 = "class Test{\n" +
                "\n" +
                "    int e;\n" +
                "    public int d;\n" +
                "    private int c;\n" +
                "\n" +
                "    public void testTreeUtils  (String a, String b){\n" +
                "        if(a.equals(\"\")  ){\n" +
                "            return   ;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(  1  ){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                          return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "\n" +
                "\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "class Test{\n" +
                "\n" +
                "    int e;\n" +
                "    public int d;\n" +
                "    private int c;\n" +
                "\n" +
                "    public void testTreeUtils  (String a, String b){\n" +
                "        if(a.equals(\"\")  ){\n" +
                "            return   ;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(  1  ){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                          return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "\n" +
                "\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));

    }

    public void testEqualCodeStaticAdded(){

        String s1 = "class Test{\n" +
                "\n" +
                "    int e;\n" +
                "    public int d;\n" +
                "    private int c;\n" +
                "\n" +
                "    Test(){\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    public static void testTreeUtils  (String a, String b){\n" +
                "        if(a.equals(\"\")  ){\n" +
                "            return   ;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(  1  ){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                          return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "\n" +
                "\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "class Test{\n" +
                "\n" +
                "    int e;\n" +
                "    public int d;\n" +
                "    private int c;\n" +
                "\n" +
                "    Test(){\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    public static void testTreeUtils  (String a, String b){\n" +
                "        if(a.equals(\"\")  ){\n" +
                "            return   ;\n" +
                "        }\n" +
                "        else{\n" +
                "            switch(  1  ){\n" +
                "                case 0:\n" +
                "                    return ;\n" +
                "                case 1:\n" +
                "                          return ;\n" +
                "                case 2:\n" +
                "                    break;\n" +
                "            }\n" +
                "            try{\n" +
                "\n" +
                "\n" +
                "\n" +
                "            } catch(NullPointerException e){\n" +
                "\n" +
                "\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));

    }
    public void testSuperComplexCase(){

        String s1 = "import org.jetbrains.annotations.NotNull;\n" +
                "import java.awt.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class Test extends Exp {\n" +
                "    int num1;\n" +
                "    int num2;\n" +
                "    String s;\n" +
                "\n" +
                "    Test () {\n" +
                " \n" +
                "        this.num1 = 0;\n" +
                "        this.num2 = 0;\n" +
                "        s = \" \";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public < T > @NotNull T accept(ExpVisitor<T> visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    Test (int num, String s) {\n" +
                "        this.num1 = num;\n" +
                "        this.num2 = num;\n" +
                "        this.s = s;\n" +
                "    }\n" +
                "\n" +
                "    public void testTreeUtils(String name, int age) {\n" +
                "        switch (age) {\n" +
                "            case 1:\n" +
                "                age += 3;\n" +
                "                System.out.println(age);\n" +
                "                break;\n" +
                "            case 2:\n" +
                "                System.out.println(age + 1);\n" +
                "                break;\n" +
                "            default:\n" +
                "                break;\n" +
                "        }\n" +
                "        System.out.println(testMethod(age));\n" +
                "        System.out.println(testMethod(name));\n" +
                "        String[] names = new String[3];\n" +
                "        for (int i = 0; i < 4; i++) {\n" +
                "            try {\n" +
                "                names[i] = name;\n" +
                "            } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "                System.out.println(e);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    static public int testMethod(int age) {\n" +
                "        int newAge = 10 * age; ArrayList<Integer> ages = new ArrayList<>();\n" +
                "        ages.add(1);\n" +
                "        ages.add(2);\n" +
                "        HashSet<Integer> newAges = (HashSet<Integer>) ages.stream().map(item -> item + 3).collect(Collectors.toSet());\n" +
                "        System.out.println(newAges);\n" +
                "        return newAge;\n" +
                "    }\n" +
                "\n" +
                "    public String testMethod(String name) {\n" +
                "        String welcome = \"Hello, \" + name + \"!\";\n" +
                "        return welcome;\n" +
                "    }\n" +
                "}";
        String s2 = "import org.jetbrains.annotations.NotNull;\n" +
                "\n" +
                "import java.awt.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class Test extends Exp {\n" +
                "    int    num1;\n" +
                "    int num2;\n" +
                "    String s;\n" +
                "\n" +
                "    Test () {\n" +
                "        this.num1 = 0;\n" +
                "        this.num2 = 0;\n" +
                "        s = \" \";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public <T> @NotNull T accept(ExpVisitor<T> visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    Test (int num, String s) {\n" +
                "        this.num1 = num;\n" +
                "        this.num2 = num;\n" +
                "        this.s = s;\n" +
                "    }\n" +
                "\n" +
                "    public void testTreeUtils(String name, int age) {\n" +
                "        switch (age) {\n" +
                "            case 1:\n" +
                "                age += 3;\n" +
                "                System.out.println(age);\n" +
                "                break;\n" +
                "            case 2:\n" +
                "                System.out.println(age + 1);\n" +
                "                break;\n" +
                "            default:\n" +
                "                break;\n" +
                "        }\n" +
                "        System.out.println(testMethod(age));\n" +
                "        System.out.println(testMethod(name));\n" +
                "        String[] names = new String[3];\n" +
                "        for (int i = 0; i < 4; i++) {\n" +
                "            try {\n" +
                "                names[i] = name;\n" +
                "            } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "                System.out.println(e);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    static public int testMethod(int age) {\n" +
                "        int newAge = 10 * age;\n" +
                "        ArrayList<Integer> ages = new ArrayList<>();\n" +
                "        ages.add(1);\n" +
                "        ages.add(2);\n" +
                "        HashSet<Integer> newAges = (HashSet<Integer>) ages.stream().map(item -> item + 3).collect(Collectors.toSet());\n" +
                "        System.out.println(newAges);\n" +
                "        return newAge;\n" +
                "    }\n" +
                "\n" +
                "    public String testMethod(String name) {\n" +
                "        String welcome = \"Hello, \" + name + \"!\";\n" +
                "        return welcome;\n" +
                "    }\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));

    }

    public void testSuperComplexCase2(){

        String s1 = "package edu.postech.csed332.homework4.expression;\n" +
                " import edu.postech.csed332.homework4.ExpVisitor;\n" +
                "import org.jetbrains.annotations.NotNull;\n" +
                "\n" +
                "import java.awt.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class Test extends Exp {\n" +
                "    int num1;\n" +
                "    int num2;\n" +
                "    String s;\n" +
                "\n" +
                "    Test () {\n" +
                "        this.num1 = 0;\n" +
                "        this.num2 = 0;\n" +
                "        s = \" \";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public <T> @NotNull T accept(ExpVisitor<T> visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    Test (int num, String s) {\n" +
                "        this.num1 = num;\n" +
                "        this.num2 = num;\n" +
                "        this.s = s;\n" +
                "    }\n" +
                "\n" +
                "    public void testTreeUtils(String name, int age) {\n" +
                "        switch (age) {\n" +
                "            case 1:\n" +
                "                age += 3;\n" +
                "                System.out.println(age);\n" +
                "                break;\n" +
                "            case 2:\n" +
                "                System.out.println(age + 1);\n" +
                "                break;\n" +
                "            default:\n" +
                "                break;\n" +
                "        }\n" +
                "        System.out.println(testMethod(age));\n" +
                "        System.out.println(testMethod(name));\n" +
                "        String[] names = new String[3];\n" +
                "        for (int i = 0; i < 4; i++) {\n" +
                "            try {\n" +
                "                names[i] = name;\n" +
                "            } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "                System.out.println(e);\n" +
                "            }\n" +
                "        }\n" +
                "        int index = 0;\n" +
                "        do {\n" +
                "            System.out.println(index);\n" +
                "            index++;\n" +
                "        } while (index < 10);\n" +
                "\n" +
                "        index = 0;\n" +
                "        while(true && true || true) {\n" +
                "            index++;\n" +
                "            if (index > 5) {\n" +
                "                break;\n" +
                "            } else {\n" +
                "                continue;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    static public int testMethod(int age) {\n" +
                "        int newAge = 10 * age;\n" +
                "        ArrayList<Integer> ages = new ArrayList<>();\n" +
                "        ages.add(1);\n" +
                "        ages.add(2);\n" +
                "        HashSet<Integer> newAges = (HashSet<Integer>) ages.stream().map(item -> item + 3).collect(Collectors.toSet());\n" +
                "        for (Integer item : newAges) {\n" +
                "            int age1 = item;\n" +
                "            Object ob = item;\n" +
                "            try {\n" +
                "                String newString = (String) ob;\n" +
                "            } catch(ClassCastException e) {\n" +
                "                throw e;\n" +
                "            }\n" +
                "        }\n" +
                "        System.out.println(newAges);\n" +
                "        return newAge;\n" +
                "    }\n" +
                "\n" +
                "    public String testMethod(String name) {\n" +
                "        String welcome = \"Hello, \" + name + \"!\";\n" +
                "        return welcome;\n" +
                "    }\n" +
                "}";
        String s2 = "package edu.postech.csed332.homework4.expression;\n" +
                "import edu.postech.csed332.homework4.ExpVisitor;\n" +
                "import org.jetbrains.annotations.NotNull;\n" +
                "\n" +
                "import java.awt.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class Test extends Exp {\n" +
                "    int num1;\n" +
                "    int num2;\n" +
                "    String s;\n" +
                "\n" +
                "    Test () {\n" +
                "        this.num1 = 0;\n" +
                "        this.num2 = 0;\n" +
                "        s = \" \";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public <T> @NotNull T accept(ExpVisitor<T> visitor) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    Test (int num, String s) {\n" +
                "        this.num1 = num;\n" +
                "        this.num2 = num;\n" +
                "        this.s = s;\n" +
                "    }\n" +
                "\n" +
                "    public void testTreeUtils(String name, int age) {\n" +
                "        switch (age) {\n" +
                "            case 1:\n" +
                "                age += 3;\n" +
                "                System.out.println(age);\n" +
                "                break;\n" +
                "            case 2:\n" +
                "                System.out.println(age + 1);\n" +
                "                break;\n" +
                "            default:\n" +
                "                break;\n" +
                "        }\n" +
                "        System.out.println(testMethod(age));\n" +
                "        System.out.println(testMethod(name));\n" +
                "        String[] names = new String[3];\n" +
                "        for (int i = 0; i < 4; i++) {\n" +
                "            try {\n" +
                "                names[i] = name;\n" +
                "            } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "                System.out.println(e);\n" +
                "            }\n" +
                "        }\n" +
                "        int index = 0;\n" +
                "        do {\n" +
                "            System.out.println(index);\n" +
                "            index++;\n" +
                "        } while (index < 10);\n" +
                "\n" +
                "        index = 0;\n" +
                "        while(true && true || true) {\n" +
                "            index++;\n" +
                "            if (index > 5) {\n" +
                "                break;\n" +
                "            } else {\n" +
                "                continue;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    static public int testMethod(int age) {\n" +
                "        int newAge = 10 * age;\n" +
                "        ArrayList<Integer> ages = new ArrayList<>();\n" +
                "        ages.add(1);\n" +
                "        ages.add(2);\n" +
                "        HashSet<Integer> newAges = (HashSet<Integer>) ages.stream().map(item -> item + 3).collect(Collectors.toSet());\n" +
                "        for (Integer item : newAges) {\n" +
                "            int age1 = item;\n" +
                "            Object ob = item;\n" +
                "            try {\n" +
                "                String newString = (String) ob;\n" +
                "            } catch(ClassCastException e) {\n" +
                "                throw e;\n" +
                "            }\n" +
                "        }\n" +
                "        System.out.println(newAges);\n" +
                "        return newAge;\n" +
                "    }\n" +
                "\n" +
                "    public String testMethod(String name) {\n" +
                "        String welcome = \"Hello, \" + name + \"!\";\n" +
                "        return welcome;\n" +
                "    }\n" +
                "}";

        assertTrue(TreeUtils.codeEqual(s1, s2, getProject()));
        assertTrue(TreeUtils.codeEqual(s2, s1, getProject()));

    }

    public void testCode1MoreExpression(){

        String s1 = "class Test{\n" +
                "\n" +
                "    public void test(){\n" +
                "        int a = 1;\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "class Test{\n" +
                "\n" +
                "    public void test(){\n" +
                "        int a = 1;\n" +
                "    }\n" +
                "\n" +
                "}";

        assertFalse(TreeUtils.codeEqual(s1, s2, getProject()));
        assertFalse(TreeUtils.codeEqual(s2, s1, getProject()));

    }

    public void testCode2MoreExpression(){

        String s1 = "class Test{\n" +
                "\n" +
                "    public void test(){\n" +
                "        int a = 1;\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "class Test{\n" +
                "\n" +
                "    public void test(){\n" +
                "        int a = 1;\n" +
                "    }\n" +
                "\n" +
                "}";

        assertFalse(TreeUtils.codeEqual(s2, s1, getProject()));
        assertFalse(TreeUtils.codeEqual(s1, s2, getProject()));

    }

    public void testCodeNonLeaf(){

        String s1 = "class Test{\n" +
                "    int a;\n" +
                "    int c;\n" +
                "    int b;\n" +
                "}";
        String s2 = "class Test{\n" +
                "    int a = 1;\n" +
                "    int c;\n" +
                "    int b;\n" +
                "}";

        assertFalse(TreeUtils.codeEqual(s2, s1, getProject()));
        assertFalse(TreeUtils.codeEqual(s1, s2, getProject()));

    }


    public void testPublicity(){

        String s1 = "public class Test{\n" +
                "    \n" +
                "}";
        String s2 = "class Test{\n" +
                "    \n" +
                "}";

        assertFalse(TreeUtils.codeEqual(s2, s1, getProject()));
        assertFalse(TreeUtils.codeEqual(s1, s2, getProject()));

    }


}
