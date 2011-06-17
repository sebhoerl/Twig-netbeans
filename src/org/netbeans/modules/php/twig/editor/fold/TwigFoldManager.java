package org.netbeans.modules.php.twig.editor.fold;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.Exceptions;

public class TwigFoldManager implements FoldManager {

    protected FoldOperation operation;
    
    @Override
    public void init(FoldOperation fo) {
        operation = fo;
        System.out.println( "TwigFoldManager::init" );
        System.out.flush();
    }

    @Override
    public void initFolds(FoldHierarchyTransaction fht) {
        System.out.println( "TwigFoldManager::initFolds" );
        System.out.flush();
        try {
            try {
                operation.addToHierarchy( new FoldType("moin") , "test", true, 1, 50, 2, 2, null, fht );
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        } finally {
            fht.commit();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {}

    @Override
    public void removeUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {}

    @Override
    public void changedUpdate(DocumentEvent de, FoldHierarchyTransaction fht) {}

    @Override
    public void removeEmptyNotify(Fold fold) {}

    @Override
    public void removeDamagedNotify(Fold fold) {}

    @Override
    public void expandNotify(Fold fold) {}

    @Override
    public void release() {}
    
    public static final class Factory implements FoldManagerFactory {

        @Override
        public FoldManager createFoldManager() {
            System.out.println( "TwigFoldManager$Factory::createFoldManager" );
            System.out.flush();
            return new TwigFoldManager();
        }

    }
    
}
