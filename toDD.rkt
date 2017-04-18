#lang typed/racket

(: toDD (Sexp -> String))
(define (toDD syntax)
  (match syntax
    [(? symbol? id) 
        (string-append
            "BinaryDecisionDiagramNode.of(new SATVariable("
            "\""
            (symbol->string id)
            "\""
            "))")]
    [(list 'or a b)
        (string-append
            (toDD a)
            ".or("
            (toDD b)
            ")")]
    [(list 'and a b)
        (string-append
            (toDD a)
            ".and("
            (toDD b)
            ")")]
    [(list 'not a)
        (string-append
            (toDD a)
            ".not()"
            )]))

(display (toDD
    '(and
        (or (or (and a (not b))
                (and b (not a)))
            y)
        (not (or y (or (and b (not a))
                       (and a (not b))))
            ))
         ))
