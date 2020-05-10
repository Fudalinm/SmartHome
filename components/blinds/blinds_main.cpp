#include <iostream>
#include <unistd.h>

int main(int argc, char const *argv[])
{
    while(1){
        std::cout << "Hello Docker here are blinds!" << std::endl;
        sleep(3);
    }
    return 0;
}