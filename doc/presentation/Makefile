SHELL = /bin/bash
.ONESHELL:

.PHONY: default watch build clean
default: build

watch:
	@function fwatch {
		function escape { echo $$1 | sed -E "s/([\\\\\'\"\$$])/\\\\\\1/g"; }
		local args=""
		while true; do
			if [[ -z "$$2" ]]; then
				args+="-c \"$$(escape $$1)\""
				break
			fi

			if [[ -d "$$1" ]]; then
				args+="-w \"$$(escape $$1)\" "
			else
				args+="-f \"$$(escape $$1)\" "
			fi
			shift
		done

		eval inotify-hookable $$args
	}
	fwatch *.md *.tex "$(MAKE) build"

build:
	latexmk -f -g -interaction=nonstopmode -pdf slides.tex

clean:
	rm *.aux *.lof *.log *.lot *.fls *.out *.toc *.fmt *.fot *.cb *.cb2 .*.lb *.snm *.synctex.gz *.vrb
