package org.netbeans.modules.php.twig.editor.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;

public class TwigParserResult extends ParserResult {

    boolean valid = true;
    
    List<Error> errorList = new ArrayList<Error>();
    List<Block> blockList = new ArrayList<Block>();
    
    TwigParserResult( Snapshot snapshot ) {
        super( snapshot );
    }
    
    public List<Error> getErrors() { return errorList; }
    
    public void addError( String description, int offset, int length ) { 
        errorList.add( new Error( description, offset, length, getSnapshot() ) ); 
    }
    
    public List<Block> getBlocks() { return blockList; }
    
    public void addBlock( CharSequence function, int offset, int length, CharSequence extra ) { 
        blockList.add( new Block( function, offset, length, extra ) ); 
    }
    
    @Override
    protected void invalidate() {
        valid = false;
    }
    
    public boolean isValid() {
        return valid;
    }

    @Override
    public List<? extends org.netbeans.modules.csl.api.Error> getDiagnostics() {
        return errorList;
    }
    
    public class Error implements org.netbeans.modules.csl.api.Error {
        
        String description;
        int offset;
        int length;
        Snapshot snapshot;
        
        public Error( String description, int offset, int length, Snapshot snapshot ) {
            this.description = description;
            this.offset = offset;
            this.length = length;
            this.snapshot = snapshot;
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getLength() {
            return length;
        }

        @Override
        public String getDisplayName() {
            return description;
        }

        @Override
        public String getKey() {
            return description;
        }

        @Override
        public FileObject getFile() {
            return snapshot.getSource().getFileObject();
        }

        @Override
        public int getStartPosition() {
            return offset;
        }

        @Override
        public int getEndPosition() {
            return offset + length;
        }

        @Override
        public boolean isLineError() {
            return false;
        }

        @Override
        public Severity getSeverity() {
            return Severity.ERROR;
        }

        @Override
        public Object[] getParameters() {
            return null;
        }
        
    }
    
    public class Block {
        
        CharSequence function;
        int offset;
        int length;
        CharSequence extra;
        
        public Block( CharSequence function, int offset, int length, CharSequence extra ) {
            this.function = function;
            this.offset = offset;
            this.length = length;
            this.extra = extra;
        }
        
        public CharSequence getExtra() { return extra; }
        
        public CharSequence getDescription() {
            return function;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getLength() {
            return length;
        }
        
    }
    
}
