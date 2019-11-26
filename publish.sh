cd builds

cd parent
echo "fermion parent"
../../gradlew $1 $2
cd ..

echo "fermion-varia"
cd varia
../../gradlew $1 $2
cd ..

echo "special-circumstances"
cd special-circumstances
../../gradlew $1 $2
cd ..

echo "fermion-simulator"
cd simulator
../../gradlew $1 $2
cd ..

echo "fermion-gui"
cd gui
../../gradlew $1 $2
cd ..

echo "fermion-modkeys"
cd modkeys
../../gradlew $1 $2
cd ..

cd ..
