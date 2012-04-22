FIGURES=$(shell ls *.png)

VIEWER=evince

view: fragmentation.pdf
	evince fragmentation.pdf

fragmentation.pdf: fragmentation.tex msrreference.bib $(FIGURES)
	pdflatex fragmentation
	pdflatex fragmentation
	bibtex fragmentation
	pdflatex fragmentation
	pdflatex fragmentation


fragmentation.tex: bare_conf_CS-beta1.1.tex topictable.tex
	cp bare_conf_CS-beta1.1.tex fragmentation.tex

clean:
	rm *.blg *.out *.bbl *.log *.aux fragmentation.pdf || echo ""
