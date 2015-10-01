
cd nem-monitor
  awk -v cur="(.*)($1)(-BETA.*)" -v new="\\\\1$2\\\\3" '{ a=gensub(cur,"\\2", "g", $0); if (a~/[0-9]+/) { $0=gensub(cur, new, $0);} print $0; }' < pom.xml > pom.out
  tr --delete "\r" < pom.out > pom.xml
  rm pom.out
  #git add pom.xml
cd - >/dev/null

cd nem-console
  awk -v cur="(.*)($1)(-BETA.*)" -v new="\\\\1$2\\\\3" '{ a=gensub(cur,"\\2", "g", $0); if (a~/[0-9]+/) { $0=gensub(cur, new, $0);} print $0; }' < pom.xml > pom.out
  tr --delete "\r" < pom.out > pom.xml
  rm pom.out
  #git add pom.xml
cd - >/dev/null


cd nem-client-api
  awk -v cur="(.*)($1)(-BETA.*)" -v new="\\\\1$2\\\\3" '{ a=gensub(cur,"\\2", "g", $0); if (a~/[0-9]+/) { $0=gensub(cur, new, $0);} print $0; }' < pom.xml > pom.out
  tr --delete "\r" < pom.out > pom.xml
  rm pom.out
  #git add pom.xml
cd - >/dev/null

#sed -n '/BETA/{ s/.*\([0-9]\+.[0-9]\+.[0-9]\+-BETA\).*/bump version to \1/; p }' nem-client-api/pom.xml
#| xargs -iXX git commit -m XX

