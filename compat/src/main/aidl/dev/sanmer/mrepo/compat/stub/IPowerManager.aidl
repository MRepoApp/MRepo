package dev.sanmer.mrepo.compat.stub;

interface IPowerManager {
    void reboot(boolean confirm, String reason, boolean wait);
}