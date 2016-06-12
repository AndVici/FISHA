function [ Bl ] = Blur( I )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
b = fspecial('gaussian', [5,5], 5);
Bl = imfilter(I,b);
end

