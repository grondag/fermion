
echo ====================================
echo "fermion parent"
echo ====================================
./gradlew publish

cd builds

echo ====================================
echo "fermion-orientation"
echo ====================================
cd orientation
../../gradlew publish
cd ..

echo ====================================
echo "fermion-varia"
echo ====================================
cd varia
../../gradlew publish
cd ..

echo ====================================
echo "special-circumstances"
echo ====================================
cd special-circumstances
../../gradlew publish
cd ..

echo ====================================
echo "fermion-simulator"
echo ====================================
cd simulator
../../gradlew publish
cd ..

echo ====================================
echo "fermion-gui"
echo ====================================
cd gui
../../gradlew publish
cd ..

echo ====================================
echo "fermion-modkeys"
echo ====================================
cd modkeys
../../gradlew publish
cd ..

cd ..
