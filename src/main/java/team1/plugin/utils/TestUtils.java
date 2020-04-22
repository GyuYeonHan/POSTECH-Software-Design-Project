package team1.plugin.utils;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

import static com.intellij.testFramework.LightPlatformTestCase.getProject;

public class TestUtils {
    /**
     * Returns PSI File from code string
     * @param codeBlock String code
     * @return return PSI File
     */
    public static PsiFile getPsiFileFromString(String codeBlock) {
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        return fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);
    }
}
