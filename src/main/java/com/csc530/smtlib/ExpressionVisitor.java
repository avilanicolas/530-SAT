package com.csc530.smtlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.DecisionDiagramNode;
import com.csc530.sat.Variable;
import com.csc530.sat.condition.bool.BooleanCondition;

/* Used by the SimpleSolver to turn an IExpr into a decision diagram */
class ExpressionVisitor implements IVisitor<DecisionDiagram> {

    private final Set<String> booleanVariables;

    public ExpressionVisitor(Set<String> booleanVariables) {
        this.booleanVariables = booleanVariables;
    }

    @Override
    public DecisionDiagram visit(IAttribute<?> e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAttributedExpr e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IBinaryLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IBinding e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IDecimal e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IExists e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Included for clarity in below boolean logic
    private DecisionDiagram not(DecisionDiagram dd) {
        return dd.not();
    }

    private DecisionDiagram and(DecisionDiagram a, DecisionDiagram b) {
        return a.and(b);
    }

    private DecisionDiagram or(DecisionDiagram a, DecisionDiagram b) {
        return a.or(b);
    }

    private DecisionDiagram xor(DecisionDiagram a, DecisionDiagram b) {
        return a.xor(b);
    }

    @Override
    public DecisionDiagram visit(IFcnExpr function)
            throws org.smtlib.IVisitor.VisitorException {
        String functionName = ((ISymbol) function.head()).value();
        List<DecisionDiagram> arguments = new ArrayList<>();
        for (IExpr expression : function.args()) {
            arguments.add(expression.accept(this));
        }

        if ("and".equalsIgnoreCase(functionName)) {
            if (arguments.size() < 2) {
                throw new RuntimeException("AND function should have at least 2 arguments");
            }
            DecisionDiagram dd = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                dd = and(dd, arguments.get(i));
            }
            return dd;
        } else if ("or".equalsIgnoreCase(functionName)) {
            if (arguments.size() < 2) {
                throw new RuntimeException("OR function should have at least 2 arguments");
            }
            DecisionDiagram dd = arguments.get(0);
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
            DecisionDiagram value = function.args().get(0).accept(this);
            return not(value);
        } else {
            throw new RuntimeException(
                    "Encountered unknown symbol while traversing expression: "
                            + function);
        }
    }

    @Override
    public DecisionDiagram visit(IForall e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IHexLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IKeyword e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(ILet e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(INumeral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IDeclaration e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IParameterizedIdentifier e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAsIdentifier e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IStringLiteral e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(ISymbol e)
            throws org.smtlib.IVisitor.VisitorException {
        String symbol = e.value();
        if (!booleanVariables.contains(symbol)) {
            throw new RuntimeException("Encountered unknown symbol: " + symbol);
        }
        return DecisionDiagramNode.of(new Variable<Boolean>(symbol, Boolean.class),
                BooleanCondition.isTrue());
    }

    @Override
    public DecisionDiagram visit(IScript e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(ICommand e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IFamily s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAbbreviation s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IApplication s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IFcnSort s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IParameter s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(ILogic s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(ITheory s)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IError e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(org.smtlib.IResponse.IError e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAssertionsResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAssignmentResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IProofResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IValueResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IUnsatCoreResponse e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DecisionDiagram visit(IAttributeList e)
            throws org.smtlib.IVisitor.VisitorException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
