package com.csc530.smtlib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Ideclare_sort;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.ICommand.Idefine_sort;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IAsIdentifier;
import org.smtlib.IExpr.IAttribute;
import org.smtlib.IExpr.IAttributeValue;
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
import org.smtlib.IPos;
import org.smtlib.IResponse;
import org.smtlib.IResponse.IAssertionsResponse;
import org.smtlib.IResponse.IAssignmentResponse;
import org.smtlib.IResponse.IAttributeList;
import org.smtlib.IResponse.IProofResponse;
import org.smtlib.IResponse.IUnsatCoreResponse;
import org.smtlib.IResponse.IValueResponse;
import org.smtlib.ISolver;
import org.smtlib.ISort.IAbbreviation;
import org.smtlib.ISort.IApplication;
import org.smtlib.ISort.IFamily;
import org.smtlib.ISort.IFcnSort;
import org.smtlib.ISort.IParameter;
import org.smtlib.ITheory;
import org.smtlib.IVisitor;
import org.smtlib.IVisitor.VisitorException;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.DecisionDiagramNode;
import com.csc530.sat.SATVariable;

class SimpleSolver implements ISolver {

    private final Set<ISymbol> booleanVariables;
    private final Set<IExpr> assertions;
    private final SMT.Configuration smtConfig;

    public SimpleSolver(SMT.Configuration smtConfig) {
        this.smtConfig = smtConfig;
        booleanVariables = new HashSet<>();
        assertions = new HashSet<>();
    }

    @Override
    public Configuration smt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse checkSatStatus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse exit() {
        // reset the solver for next use
        booleanVariables.clear();
        assertions.clear();
        return smtConfig.responseFactory.success();
    }

    @Override
    public IResponse set_logic(String logicName, IPos pos) {
        if ("QF_UF".equals(logicName)) {
            return smtConfig.responseFactory.success();
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public IResponse push(int number) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse pop(int number) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse assertExpr(IExpr expr) {
        assertions.add(expr);
        return smtConfig.responseFactory.success();
    }

    @Override
    public IResponse check_sat() {
        Map<ISymbol, SATVariable> symbolMap = new HashMap<>();
        for (ISymbol sym : booleanVariables) {
            symbolMap.put(sym, new SATVariable(sym.value()));
        }

        // Builds a decision diagram by traversing each assertion, then anding
        // all assertion diagrams into currentDiagram
        DecisionDiagram currentDiagram = null;
        for (IExpr expression : assertions) {
            IVisitor<DecisionDiagram> visitor = new IVisitor<DecisionDiagram>() {

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

                @Override
                public DecisionDiagram visit(IFcnExpr function)
                        throws org.smtlib.IVisitor.VisitorException {
                    ISymbol functionName = (ISymbol) function.head();
                    if ("and".equalsIgnoreCase(functionName.value())) {
                        if (function.args().size() != 2) {
                            throw new RuntimeException("AND function should have 2 arguments");
                        }
                        DecisionDiagram left = function.args().get(0).accept(this);
                        DecisionDiagram right = function.args().get(1).accept(this);
                        return left.and(right);
                    } else if ("not".equalsIgnoreCase(functionName.value())) {
                        if (function.args().size() != 1) {
                            throw new RuntimeException("NOT function should have 2 arguments");
                        }
                        DecisionDiagram value = function.args().get(0).accept(this);
                        return value.not();
                    } else {
                        throw new RuntimeException("Encountered unknown symbol while traversing expression: " + function);
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
                    if (!symbolMap.containsKey(e)) {
                        throw new RuntimeException ("Encountered unknown symbol: " + e);
                    }
                    return DecisionDiagramNode.of(symbolMap.get(e));
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

            };
            try {
                DecisionDiagram diagram = expression.accept(visitor);
                if (currentDiagram == null) {
                    currentDiagram = diagram;
                } else {
                    currentDiagram = currentDiagram.and(diagram);
                }
            } catch (VisitorException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        if (currentDiagram == null) {
            return smtConfig.responseFactory.error("No assertions statements found to check for satisfiability");
        }

        if (currentDiagram.isSatisfiable()) {
            return smtConfig.responseFactory.sat();
        } else {
            return smtConfig.responseFactory.unsat();
        }
    }

    @Override
    public IResponse declare_fun(Ideclare_fun cmd) {
        if (!cmd.argSorts().isEmpty()) {
            throw new UnsupportedOperationException("Functions with parameters are not yet implemented");
        }

        if (!cmd.resultSort().isBool()) {
            throw new UnsupportedOperationException("Functions with a non-boolean return type are not yet implemented");
        }

        booleanVariables.add(cmd.symbol());

        return smtConfig.responseFactory.success();
    }

    @Override
    public IResponse declare_sort(Ideclare_sort cmd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse define_fun(Idefine_fun cmd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse define_sort(Idefine_sort cmd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse set_option(IKeyword option, IAttributeValue value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse set_info(IKeyword key, IAttributeValue value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_assertions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_proof() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_unsat_core() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_value(IExpr... terms) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_assignment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_option(IKeyword option) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IResponse get_info(IKeyword option) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
