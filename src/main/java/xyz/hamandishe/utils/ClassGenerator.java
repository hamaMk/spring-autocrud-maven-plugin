package xyz.hamandishe.utils;

public interface ClassGenerator {
    FileUtils fileUtils = new FileUtils();
    void generate(String className);

    void save();
}
