package com.csc530.smtlib;

import java.util.HashSet;
import java.util.Set;

import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Ideclare_sort;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.ICommand.Idefine_sort;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IAttributeValue;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IPos;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.IVisitor;
import org.smtlib.IVisitor.VisitorException;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;

import com.csc530.sat.DecisionDiagram;

/**
 * This class exists purely to interface with the jSMTLIB parsing library. It
 * should only be used by the SmtLibParser class.
 */
class SimpleSolver implements ISolver {

    private static final String QUANTIFIER_FREE_BOOLEAN_ONLY_LOGIC_TYPE_IDENTIFIER = "QF_UF";

    private final Set<IExpr> assertions;
    private final Set<String> booleanVariables;
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
        if (QUANTIFIER_FREE_BOOLEAN_ONLY_LOGIC_TYPE_IDENTIFIER.equals(logicName)) {
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
        // Builds a decision diagram by traversing each assertion, then anding
        // all assertion diagrams into currentDiagram
        DecisionDiagram currentDiagram = null;
        for (IExpr expression : assertions) {
            IVisitor<DecisionDiagram> visitor = new ExpressionVisitor(booleanVariables);
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
            return smtConfig.responseFactory
                    .error("No assertions statements found to check for satisfiability");
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
            throw new UnsupportedOperationException(
                    "Functions with parameters are not yet implemented");
        }

        if (!cmd.resultSort().isBool()) {
            throw new UnsupportedOperationException(
                    "Functions with a non-boolean return type are not yet implemented");
        }

        booleanVariables.add(cmd.symbol().value());

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
