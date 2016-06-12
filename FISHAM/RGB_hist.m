function [ RGB ] = RGB_hist( I )
%UNTITLED3 Summary of this function goes here
%   Detailed explanation goes here
[R, C, D] = size(I);
if D == 1
    RGB = [imhist(I(:,:,1)),imhist(I(:,:,1)),imhist(I(:,:,1))];
else
    RGB = [imhist(I(:,:,1)),imhist(I(:,:,2)),imhist(I(:,:,3))]; 
end
end

