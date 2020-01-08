
echo "fermion parent"
./gradlew publish

cd builds

echo "fermion-varia"
cd varia
../../gradlew publish
cd ..

echo "special-circumstances"
cd special-circumstances
../../gradlew publish
cd ..

echo "fermion-simulator"
cd simulator
../../gradlew publish
cd ..

echo "fermion-gui"
cd gui
../../gradlew publish
cd ..

echo "fermion-modkeys"
cd modkeys
../../gradlew publish
cd ..

cd ..
