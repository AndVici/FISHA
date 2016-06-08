function [ file_path ] = main( file_path )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here

addpath(file_path);
F = fullfile(file_path, '*.jpg');
path = 'C:\Users\willie\Courses\Spring 2016\Principals of Data Mining\Project\FISHA\test\';
files = dir(F);

for i = 1:length(files)
   % fprintf('file = %s\n', files(i).name);
    im = imread(files(i).name);
    [row, col, CC] = size(im);
    if row > 1000
        image = imresize(im,[1000 NaN]);
    else
        image = im;
    end
    if col > 1000
        image = imresize(image, [NaN 1000]);
    end
    %image = imresize(im, 0.35);
    [RGB] = RGB_hist(image);
    s = strcat(path, files(i).name, '_RGB_h.csv');
    fid = fopen(s, 'w');
    fclose(fid);
    csvwrite(s, RGB);
    [V, H] = sobel(image);
    s = strcat(path,files(i).name,'_V.png');
    imwrite(V, s);
    s = strcat(path, files(i).name, '_H.png');
    imwrite(H, s);
    s = strcat(path, files(i).name, '_blur.png');
    Bl = Blur(image);
    imwrite(Bl, s);
end
end

