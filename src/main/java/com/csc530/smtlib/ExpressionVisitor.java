package com.csc530.smtlib;

import java.util.ArrayList;
import java.util.List;

import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IAsIdentifier;
import org.smtlib.IExpr.IAttribute;
import org.smtlib.IExpr.IAttributedExpr;
import org.smtlib.IExpr.IBinaryLiteral;
import org.smtlib.IExpr.IBinding;
import org.smtlib.IExpr.IDecimal;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IError;
import org.smtlib.IExpr.IExists;
import org.smtlib.IExpr.IFcnExpr;
import org.smtlib.IExpr.IForall;
import org.smtlib.IExpr.IHexLiteral;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.ILet;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.IParameterizedIdentifier;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ILogic;
import org.smtlib.IResponse;
import org.smtlib.IResponse.IAssertionsResponse;
import org.smtlib.IResponse.IAssignmentResponse;
import org.smtlib.IResponse.IAttributeList;
import org.smtlib.IResponse.IProofResponse;
import org.smtlib.IResponse.IUnsatCoreResponse;
import org.smtlib.IResponse.IValueResponse;
import org.smtlib.ISort.IAbbreviation;
import org.smtlib.ISort.IApplication;
import org.smtlib.ISort.IFamily;
import org.smtlib.ISort.IFcnSort;
import org.smtlib.ISort.IParameter;
import org.smtlib.ITheory;
import org.smtlib.IVisitor;

import com.csc530.bruteforce.AndClause;
import com.csc530.bruteforce.Clause;
import com.csc530.bruteforce.NotClause;
import com.csc530.bruteforce.OrClause;
import com.csc530.bruteforce.ReferenceClause;
import com.csc530.bruteforce.XorClause;

/* Used by the SimpleSolver to turn an IExpr into a decision diagram */
class ExpressionVisitor implements IVisitor<Clause> {

    @Override
    public Clause visit(IAttribute<?> e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAttributedExpr e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IBinaryLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IBinding e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IDecimal e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IExists e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Included for clarity in below boolean logic
    private Clause not(Clause dd) {
        return new NotClause(dd);
    }

    private Clause and(Clause a, Clause b) {
        return new AndClause(a, b);
    }

    private Clause or(Clause a, Clause b) {
        return new OrClause(a, b);
    }

    private Clause xor(Clause a, Clause b) {
        return new XorClause(a, b);
    }

    @Override
    public Clause visit(IFcnExpr function)
            throws org.smtlib.IVisitor.VisitorException {
        String functionName = ((ISymbol) function.head()).value();
        List<Clause> arguments = new ArrayList<>();
        for (IExpr expression : function.args()) {
            arguments.add(expression.accept(this));
        }

        if ("and".equalsIgnoreCase(functionName)) {
            if (arguments.size() < 2) {
                throw new RuntimeException("AND function should have at least 2 arguments");
            }
            Clause dd = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                dd = and(dd, arguments.get(i));
            }
            return dd;
        } else if ("or".equalsIgnoreCase(functionName)) {
            if (arguments.size() < 2) {
                throw new RuntimeException("OR function should have at least 2 arguments");
            }
            Clause dd = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                dd = or(dd, arguments.get(i));
            }
            return dd;
        } else if ("xor".equalsIgnoreCase(functionName)) {
            if (arguments.size() != 2) {
                throw new RuntimeException("XOR function should have 2 arguments");
            }
            return xor(arguments.get(0), arguments.get(1));
        } else if ("=>".equalsIgnoreCase(functionName)) {
            // implies
            if (arguments.size() != 2) {
                throw new RuntimeException("=> function should have 2 arguments");
            }
            return or(and(arguments.get(0), arguments.get(1)), not(arguments.get(0)));
        } else if ("ite".equalsIgnoreCase(functionName)) {
            // if then else
            if (arguments.size() != 3) {
                throw new RuntimeException("=> function should have 2 arguments");
            }
            return or(
                    and(arguments.get(0), arguments.get(1)),
                    and(not(arguments.get(0)), arguments.get(2)));
        } else if ("not".equalsIgnoreCase(functionName)) {
            if (arguments.size() != 1) {
                throw new RuntimeException("NOT function should have 1 argument");
            }
            Clause value = function.args().get(0).accept(this);
            return not(value);
        } else {
            throw new RuntimeException(
                    "Encountered unknown symbol while traversing expression: "
                            + function);
        }
    }

    @Override
    public Clause visit(IForall e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IHexLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IKeyword e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(ILet e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(INumeral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IDeclaration e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IParameterizedIdentifier e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAsIdentifier e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IStringLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(ISymbol e)
            throws org.smtlib.IVisitor.VisitorException {
        String symbol = e.value();
        return new ReferenceClause(symbol);
    }

    @Override
    public Clause visit(IScript e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(ICommand e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IFamily s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAbbreviation s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IApplication s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IFcnSort s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IParameter s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(ILogic s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(ITheory s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IError e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(org.smtlib.IResponse.IError e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAssertionsResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAssignmentResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IProofResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IValueResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IUnsatCoreResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Clause visit(IAttributeList e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
