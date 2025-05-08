import { LitElement, html, css } from "lit";
import { unsafeCSS } from "lit";

import Map from "https://cdn.skypack.dev/ol/Map.js";
import View from "https://cdn.skypack.dev/ol/View.js";
import TileLayer from "https://cdn.skypack.dev/ol/layer/Tile.js";
import VectorLayer from "https://cdn.skypack.dev/ol/layer/Vector.js";
import OSM from "https://cdn.skypack.dev/ol/source/OSM.js";
import VectorSource from "https://cdn.skypack.dev/ol/source/Vector.js";
import Feature from "https://cdn.skypack.dev/ol/Feature.js";
import Point from "https://cdn.skypack.dev/ol/geom/Point.js";
import Style from "https://cdn.skypack.dev/ol/style/Style.js";
import Icon from "https://cdn.skypack.dev/ol/style/Icon.js";
import Overlay from "https://cdn.skypack.dev/ol/Overlay.js";
import Zoom from "https://cdn.skypack.dev/ol/control/Zoom.js";
import { fromLonLat, toLonLat } from "https://cdn.skypack.dev/ol/proj.js";

// Manually copied OpenLayers CSS
const openLayersCss = `
.ol-control {
  position: absolute;
  top: 0.5em;
  left: 0.5em;
  right: auto;
  bottom: auto;
}
.ol-zoom {
  top: 1em;
}
.ol-rotate {
  top: 4.5em;
}
.ol-control button {
  display: block;
  margin: 1px;
  padding: 0;
  color: #000;
  font-size: 1.1em;
  font-weight: bold;
  text-decoration: none;
  text-align: center;
  height: 2em;
  width: 2em;
  line-height: 2em;
  background-color: rgba(255,255,255,0.4);
  border-radius: 2px;
  border: none;
}
.ol-control button:hover,
.ol-control button:focus {
  text-decoration: none;
  background-color: rgba(255,255,255,0.7);
  color: black;
  outline: none;
}
.ol-control button::-moz-focus-inner {
  border: none;
  padding: 0;
}
.ol-rotate.ol-hidden {
  display: none;
}
`;

// Initialize WebSocket
const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const socket = new WebSocket(protocol + "://" + window.location.host + "/map");
socket.onopen = function (event) {
  console.log("WebSocket connection opened");
};

export class DemoMap extends LitElement {
  static styles = [
    css`
      :host {
        display: block;
        width: 100%;
        height: 100%;
      }
      #map {
        width: 100%;
        height: 100%;
      }
      .marker-label {
        background-color: rgba(255, 255, 255, 0.8);
        border: 1px solid rgba(0, 0, 0, 0.2);
        padding: 5px;
        border-radius: 5px;
        white-space: nowrap;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
        font-size: 12px;
        color: #333;
      }
    `,
    unsafeCSS(openLayersCss), // Apply OpenLayers CSS
  ];

  constructor() {
    super();
    this.vectorSource = new VectorSource(); // Define vectorSource here
  }

  firstUpdated() {
    // Create a vector layer and add the vector source to it
    const vectorLayer = new VectorLayer({
      source: this.vectorSource,
    });

    this.map = new Map({
      target: this.shadowRoot.getElementById("map"),
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
        vectorLayer, // Add the vector layer to the map
      ],
      view: new View({
        center: [0, 0],
        zoom: 2,
      }),
      controls: [
        new Zoom(), // Add only the Zoom control
      ],
    });

    // Dispatch a custom event to notify that the map is ready
    this.dispatchEvent(new CustomEvent("map-ready", { detail: { map: this.map } }));

    // Add a moveend event listener to get the map extent when zoom occurs
    this.map.on("moveend", () => {
      const extent = this.map.getView().calculateExtent(this.map.getSize());
      const bottomLeft = toLonLat([extent[0], extent[1]]);
      const topRight = toLonLat([extent[2], extent[3]]);
      const extentObject = {
        west: bottomLeft[0],
        south: bottomLeft[1],
        east: topRight[0],
        north: topRight[1],
        type: "extent",
      };

      console.log("Map extent in lat/lon: ", extentObject);

      // Check if the WebSocket is open before sending the message
      if (socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify(extentObject));
      } else {
        console.warn("WebSocket is not open. ReadyState: ", socket.readyState);
      }
    });
  }

  render() {
    return html`<div id="map"></div>`;
  }
}

customElements.define("demo-map", DemoMap);

// Function to check if a string is valid JSON
function isValidJSON(message) {
  try {
    JSON.parse(message);
    return true;
  } catch (error) {
    return false;
  }
}

// Define a global array to store markers
const markers = [];

socket.onmessage = (event) => {
  if (isValidJSON(event.data)) {
    console.log("Received JSON message: ", event.data);
    const data = JSON.parse(event.data);
    if (data.type === "marker") {
      const coordinates = fromLonLat(data.coordinates); // Transform the coordinates

      // Create a marker feature
      const marker = new Feature({
        geometry: new Point(coordinates), // Coordinates in [longitude, latitude] format
      });

      // Create a style for the marker
      const markerStyle = new Style({
        image: new Icon({
          src: "/images/marker.png", // URL to the marker icon
          anchor: [0.5, 1], // Anchor the icon at the bottom center
        }),
      });

      // Apply the style to the marker
      marker.setStyle(markerStyle);

      // Add the marker to the vector source
      const demoMap = document.querySelector("demo-map");
      demoMap.vectorSource.addFeature(marker);

      // Center the map on the marker and zoom in
      demoMap.map.getView().setCenter(coordinates);
      demoMap.map.getView().setZoom(12);
    } else if (data.type === "military") {
      const coordinates = fromLonLat(data.coordinates); // Transform the coordinates

      // Create a military symbol using Milsymbol
      const symbol = new ms.Symbol(data.sidc);
      symbol.setOptions({
        size: 25, // Size of the symbol
      });
      if (data.unit_name) {
        symbol.setOptions({
          uniqueDesignation: data.unit_name, // Size of the symbol
        });
      }

      const symbolCanvas = symbol.asCanvas(); // Get the symbol as a canvas
      // Convert the canvas to a data URL
      const symbolDataURL = symbolCanvas.toDataURL();

      // Create a marker feature for the military symbol
      const marker = new Feature({
        geometry: new Point(coordinates), // Coordinates in [longitude, latitude] format
      });

      // Create a style for the military symbol
      const markerStyle = new Style({
        image: new Icon({
          src: symbolDataURL, // Use the Milsymbol data URL as the icon source
          anchor: [0.5, 1], // Anchor the icon at the bottom center
        }),
      });

      // Apply the style to the marker
      marker.setStyle(markerStyle);

      // Add the marker to the vector source
      const demoMap = document.querySelector("demo-map");
      demoMap.vectorSource.addFeature(marker);

      // Center the map on the marker and zoom in
      // demoMap.map.getView().setCenter(coordinates);
      // demoMap.map.getView().setZoom(12);

      // Store the marker and its associated summary in the global array
      markers.push({ marker, summary: data.summary });

    } else {
      console.warn("Received non-JSON message: ", event.data);
    }
  }
};

socket.onclose = function (event) {
  console.log("WebSocket connection closed: ", event);
};

socket.onerror = function (event) {
  console.error("WebSocket error: ", event);
};

const demoMap = document.querySelector("demo-map");

// Wait for the map to be ready
demoMap.addEventListener("map-ready", (event) => {
  const map = event.detail.map; // Get the map instance from the event

  map.on("click", (event) => {
    let featureFound = false;

    // Check if the clicked feature is one of the markers
    map.forEachFeatureAtPixel(event.pixel, (feature) => {
      const matchedMarker = markers.find((item) => item.marker === feature);
      if (matchedMarker) {
        const summaryElement = document.createElement("div");
        summaryElement.className = "marker-label";
        summaryElement.innerHTML = matchedMarker.summary; // Set the summary text

        const summaryOverlay = new Overlay({
          element: summaryElement,
          positioning: "bottom-center",
          position: feature.getGeometry().getCoordinates(), // Set the position of the overlay
        });

        // Add the overlay to the map
        map.addOverlay(summaryOverlay);

        summaryElement.style.display = "block"; // Show the overlay
        featureFound = true;
      }
    });

    if (!featureFound) {
      // Hide all overlays if clicking elsewhere
      map.getOverlays().clear();
    }
  });
});