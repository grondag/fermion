
echo ====================================
echo "fermion parent"
echo ====================================
./gradlew publish --rerun-tasks

cd builds

echo ====================================
echo "fermion-orientation"
echo ====================================
cd orientation
../../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "fermion-varia"
echo ====================================
cd varia
../../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "special-circumstances"
echo ====================================
cd special-circumstances
../../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "fermion-simulator"
echo ====================================
cd simulator
../../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "fermion-gui"
echo ====================================
cd gui
../../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "fermion-modkeys"
echo ====================================
cd modkeys
../../gradlew publish --rerun-tasks
cd ..

cd ..
