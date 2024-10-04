cmake_minimum_required(VERSION 3.4.1)

add_library(user_validation SHARED user_validation.c)

find_library(log-lib log)

target_link_libraries(user_validation ${log-lib})
