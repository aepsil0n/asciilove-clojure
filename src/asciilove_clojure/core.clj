(ns asciilove-clojure.core
  (:gen-class))

(use 'mikera.image.core)
(use 'mikera.image.colours)
(require '[mikera.image.filters :as filt])
(require '[clojure.math.numeric-tower :as math])


(defn ascii-rescale
    "rescales image suitable for ascii conversion"
    [image]
    (scale image (/ 1 5) (/ 1 8)))

(defn luminosity
    "perceived luminosity"
    [colour]
    (reduce + (map * (map #(/ % 255) (components-rgb colour)) [0.299 0.587 0.114])))

(def ascii-chars " .:oxIO▒▓")

(defn lum->ascii
    "associate a luminosity with an ASCII character"
    [lum]
    (get ascii-chars (-> lum (* (count ascii-chars)) math/floor int)))

(defn image->ascii-art
    "converts an entire image to ASCII art"
    [image]
    (->> image
        get-pixels
        (map (comp lum->ascii luminosity))
        (partition (width image))
        (map #(reduce str %))
        (reduce #(str %1 "\n" %2))))

(def image-required "error: no image specified!")

(defn -main
    "displays an image as ascii art"
    [& args]
    (let [path (first args)] (if (nil? path)
        (println image-required)
        (-> path
            load-image-resource
            ascii-rescale
            image->ascii-art
            println))))
