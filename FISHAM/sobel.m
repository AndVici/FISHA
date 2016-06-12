function [V, H] = sobel( I1 )
%UNTITLED4 Summary of this function goes here
%   Detailed explanation goes here
[rows, cols, CCs] = size(I1);
if CCs > 1
    I = rgb2gray(I1);
else
    I = I1;
end
% max = -1;
% 
% temp = double(I);
% V = double(I);
% H = double(I);
% grey = double(I);
% for i = 2:size(temp,1)-1
%     for j = 2:size(temp,2)-1
%         V(i-1,j-1) = ((temp(i+1,j-1)+2*temp(i+1,j)+temp(i+1, j+1))-(temp(i-1,j-1)+2*temp(i-1,j)+temp(i-1,j+1)));
%         H(i-1,j-1) = ((temp(i-1,j+1)+2*temp(i,j+1)+temp(i+1, j+1))-(temp(i-1,j-1)+2*temp(i,j-1)+temp(i+1,j-1)));
%         grey(i-1,j-1) = sqrt(H(i-1,j-1)*H(i-1,j-1)+V(i-1,j-1)*V(i-1,j-1));
%         if max < grey(i-1,j-1)
%             max = grey(i-1,j-1);
%         end
%     end
% end
% end
h = fspecial('sobel');
H = imfilter(I,h);

v = h';
V = imfilter(I,v);
