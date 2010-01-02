which="$(which "$0" 2>/dev/null)"
[[ "$which" = "" ]] && location="$0" || location="$which"
dir="$(dirname "$(readlink -m "$location")")"

if [ -f "$dir/bwproxy" -a -x "$dir/bwproxy" ]; then
	LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$dir/lib/cmdargs/dist" "$dir/bwproxy" "$@"
else
	java -classpath "$dir/lib/cmdargs/dist/cmdargs.jar:$dir/dist/bwproxy.jar" de.cdauth.bwproxy.Main "$@"
fi
